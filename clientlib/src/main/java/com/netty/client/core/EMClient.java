package com.netty.client.core;

import android.content.Context;

import com.netty.client.codec.NettyDecoder;
import com.netty.client.codec.NettyEncoder;
import com.netty.client.common.ConnectionWatchdog;
import com.netty.client.common.InnerMessageHelper;
import com.netty.client.core.threadpool.ExecutorFactory;
import com.netty.client.handler.ConnectionManagerHandler;
import com.netty.client.handler.IdleStateTrigger;
import com.netty.client.handler.MessageRecvHandler;
import com.netty.client.handler.StatisticsRecvHandler;
import com.netty.client.handler.StatisticsSendHandler;
import com.netty.client.httpserver.HttpServer;
import com.netty.client.innermsg.CallbackMessage;
import com.netty.client.msg.EMDevice;
import com.netty.client.utils.HostUtils;
import com.netty.client.utils.L;
import com.netty.client.utils.NetUtils;

import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * Created by robincxiao on 2017/8/24.
 */

public class EMClient extends BaseConnector implements ChannelHandlerHolder {
    //private static final String DEAULT_HOST = "192.168.1.112";
    private static final int DEAULT_PORT = 9987;
    private static final int STATUS_NONE = 1;
    private static final int STATUS_CONNECTING = 2;
    private static final int STATUS_CONNECTED = 3;
    private static final long WRITER_IDLE_TIME = 10;
    private static final long READER_IDLE_TIME = 20;
    private static final int TRIGGER_FROM_USER = 1;//用户启动连接
    private static final int TRIGGER_FROM_SERVICE = 2;//service启动连接
    private static final int TRIGGER_FROM_DISCONNECT_RETRY = 3;//断线重连
    private static final int TRIGGER_FROM_TIMER_DETECTION = 4;//定时检测
    private static final int TRIGGER_FROM_NET_RECONNECTED = 5;//设备网络重新建立
    private volatile static EMClient sInstance;
    private Context mContext;
    private ChannelFuture mFuture;
    private ConnectionWatchdog mWatchdog;
    private AtomicInteger mStatus;
    private EMDevice mDevice;//当前连接设备

    private EMClient() {
        super();
        //启动http server
        HttpServer.getInstance().start();
    }

    public static EMClient getInstance() {
        if (sInstance == null) {
            synchronized (EMClient.class) {
                if (sInstance == null) {
                    sInstance = new EMClient();
                }
            }
        }

        return sInstance;
    }

    public void connectDevice(String host){
        connectDevice(new EMDevice(host, ""));
    }

    public void connectDevice(EMDevice newDevice) {
        if (mDevice == null) {
            this.mDevice = newDevice;
            connect(TRIGGER_FROM_USER);
        } else {
            //如果连接的新设备与以前的设备相同，则直接连接
            if (mDevice.id.equals(newDevice.id)) {
                connect(TRIGGER_FROM_USER);
            } else {
                //如果连接的不同设备，先关闭当前连接，然后再初始化
                shutdownGracefully();
                initInner();
                this.mDevice = newDevice;
                connect(TRIGGER_FROM_USER);
            }
        }
    }

    public EMDevice getDevice() {
        return mDevice;
    }

    public EMMessageManager getEMMessageManager() {
        return EMMessageManager.getInstance();
    }

    public EMConnectManager getEMConnectManager() {
        return EMConnectManager.getInstance();
    }

    public Context getContext() {
        return mContext;
    }

    public void init(Context context) {
        mContext = context.getApplicationContext();
        mStatus = new AtomicInteger(STATUS_NONE);

        mWatchdog = new ConnectionWatchdog(context);
        mWatchdog.setListener(new ConnectionWatchdog.WatchdogListener() {
            @Override
            public void channelActive(ChannelHandlerContext ctx) {

            }

            @Override
            public void channelInActive(ChannelHandlerContext ctx) {
                mStatus.getAndSet(STATUS_NONE);//断线后需要重新设置状态，这样Watchdog才能重连(这个设置很重要)
            }

            @Override
            public void disconnectRetry() {
                connect(TRIGGER_FROM_DISCONNECT_RETRY);
            }

            @Override
            public void timerCheck() {
                connect(TRIGGER_FROM_TIMER_DETECTION);
            }
        });
    }

    /**
     * 判断连接状态
     *
     * @return
     */
    public boolean isActive() {
        return mFuture != null && mFuture.channel() != null && mFuture.channel().isActive();
        //return mStatus != null && mStatus.compareAndSet(STATUS_CONNECTED, STATUS_CONNECTED);
    }

    public String localHost() {
        if (mFuture != null && mFuture.channel() != null && mFuture.channel().localAddress() != null) {
            return HostUtils.parseHostPort(mFuture.channel().localAddress().toString());
        }

        return "";
    }

    /**
     * 获取server host(已连接状态时)
     * @return
     */
    public String remoteHost(){
        if(isActive()){
            SocketAddress address = mFuture.channel().remoteAddress();
            if(address != null) {
                return HostUtils.parseHost(address.toString());
            }

        }
        return "";
    }

    @Override
    public void connect() {
        connect(TRIGGER_FROM_SERVICE);
    }

    private void connect(final int triggerType) {
        synchronized (bootstrap()) {
            if (mContext == null) {
                L.writeFile("return mContext == null , triggerType = " + triggerType);
                return;
            }

            if (!NetUtils.isWifi(mContext)) {
                handlerUserSpaceCallback(triggerType, CallbackMessage.MSG_TYPE_NOT_WIFI);
                L.writeFile("return when net is not wifi , triggerType = " + triggerType);
                return;
            }

            if (mStatus.compareAndSet(STATUS_CONNECTING, STATUS_CONNECTING)) {
                handlerUserSpaceCallback(triggerType, CallbackMessage.MSG_TYPE_CONNECTING);
                L.writeFile("return when connecting , triggerType = " + triggerType);
                return;
            }

            if (mStatus.compareAndSet(STATUS_CONNECTED, STATUS_CONNECTED)) {
                handlerUserSpaceCallback(triggerType, CallbackMessage.MSG_TYPE_CONNECTED);
                L.writeFile("return when connected , triggerType = " + triggerType);
                return;
            }

            if (mDevice == null) {
                handlerUserSpaceCallback(triggerType, CallbackMessage.MSG_TYPE_HOST_NULL);
                L.writeFile("mDevice = null , triggerType = " + triggerType);
                return;
            }

            L.writeFile("connecting.....................");
            mStatus.getAndSet(STATUS_CONNECTING);

            bootstrap().handler(new ChannelInitializer<Channel>() {

                @Override
                protected void initChannel(Channel ch) throws Exception {
                    ch.pipeline().addLast(handlers());
                }
            });
            mFuture = bootstrap().connect(mDevice.id, DEAULT_PORT);
        }

        /**
         * 注：进行connect连接时，如果连接成功则会调用设置的handler中的channelActive方法
         * 但是连接未成功时，是不会调用设置的handler中的channelInActive方法，因为连接并不是从已连接到断开的过程
         */
        mFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    mStatus.getAndSet(STATUS_CONNECTED);
                    switch (triggerType) {
                        case TRIGGER_FROM_USER:
                            L.writeFile("user启动连接成功");
                            //InnerMessageHelper.sendConnectSuccByUserMessage(mDevice.id);
                            break;
                        case TRIGGER_FROM_SERVICE:
                            L.writeFile("service启动连接成功");
                            break;
                        case TRIGGER_FROM_DISCONNECT_RETRY:
                            L.writeFile("断线重连成功");
                            break;
                        case TRIGGER_FROM_TIMER_DETECTION:
                            L.writeFile("定时重连成功");
                            break;
                    }
                } else {
                    mStatus.getAndSet(STATUS_NONE);
                    switch (triggerType) {
                        case TRIGGER_FROM_USER:
                            InnerMessageHelper.sendErrorCallbackMessage(CallbackMessage.MSG_TYPE_CONNECT_FAIL);
                            L.writeFile("user启动连接失败，启动断线重连");
                            mWatchdog.retry();
                            break;
                        case TRIGGER_FROM_SERVICE:
                            L.writeFile("service启动连接失败，启动断线重连");
                            mWatchdog.retry();
                            break;
                        case TRIGGER_FROM_DISCONNECT_RETRY:
                            L.writeFile("断线重连失败");
                            mWatchdog.retry();
                            break;
                        case TRIGGER_FROM_TIMER_DETECTION:
                            L.writeFile("定时重连失败");
                            break;
                    }
                    Throwable throwable = channelFuture.cause();
                    if (throwable != null) {
                        L.writeFile(throwable.toString());
                    }
                }
            }
        });
    }

    /**
     * 处理用户空间连接回调
     * @param type
     */
    private void handlerUserSpaceCallback(final int triggerType, int type){
        if(triggerType == TRIGGER_FROM_USER){
            InnerMessageHelper.sendErrorCallbackMessage(type);
        }
    }

    @Override
    public ChannelHandler[] handlers() {
        return new ChannelHandler[]{
                mWatchdog,
                new IdleStateHandler(READER_IDLE_TIME, WRITER_IDLE_TIME, 0, TimeUnit.SECONDS),
                new IdleStateTrigger(),
                new ConnectionManagerHandler(),
                new NettyEncoder(),
                new NettyDecoder(),
                new StatisticsSendHandler(),
                new StatisticsRecvHandler(),
                new MessageRecvHandler()
        };
    }

    /**
     * 获取本地文件url
     * @param path 本地文件路径
     * @return
     */
    public String getLocalUrl(String path){
        if (mFuture != null && mFuture.channel() != null && mFuture.channel().localAddress() != null) {
            return HttpServer.getInstance().getLocalUrl(HostUtils.parseHost(mFuture.channel().localAddress().toString()), path);
        }

        return "";
    }

    @Override
    public void shutdownGracefully() {
        mStatus.getAndSet(STATUS_NONE);
        super.shutdownGracefully();
    }

    public void onDestory() {
        shutdownGracefully();
        ExecutorFactory.shutdownNow();
        mWatchdog.onDestory();
        HttpServer.getInstance().destory();
    }
}
