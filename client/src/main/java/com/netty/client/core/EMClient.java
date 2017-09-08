package com.netty.client.core;

import android.content.Context;

import com.netty.client.codec.ProtobufDecoder;
import com.netty.client.codec.ProtobufEncoder;
import com.netty.client.common.ConnectionWatchdog;
import com.netty.client.common.InnerMessageHelper;
import com.netty.client.core.threadpool.ExecutorFactory;
import com.netty.client.handler.ConnectionManagerHandler;
import com.netty.client.handler.IdleStateTrigger;
import com.netty.client.handler.MessageRecvHandler;
import com.netty.client.msg.CallbackTaskMessage;
import com.netty.client.msg.EMDevice;
import com.netty.client.utils.HostUtils;
import com.netty.client.utils.L;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.timeout.IdleStateHandler;
import xiao.framework.util.NetUtils;

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

    public String localHost() {
        if (mFuture != null) {
            return HostUtils.parseHostPort(mFuture.channel().localAddress().toString());
        }

        return "";
    }

    public void init(Context context) {
        mContext = context;
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
        return mStatus != null && mStatus.compareAndSet(STATUS_CONNECTED, STATUS_CONNECTED);
    }

    @Override
    public void connect() {
        connect(TRIGGER_FROM_SERVICE);
    }

    private void connect(final int triggerType) {
        synchronized (bootstrap()) {
            if (!NetUtils.isWifi(mContext)) {
                handlerUserSpaceCallback(triggerType, CallbackTaskMessage.MSG_TYPE_NOT_WIFI);
                L.print("return when net is not wifi , triggerType = " + triggerType);
                return;
            }

            if (mStatus.compareAndSet(STATUS_CONNECTING, STATUS_CONNECTING)) {
                handlerUserSpaceCallback(triggerType, CallbackTaskMessage.MSG_TYPE_CONNECTING);
                L.print("return when connecting , triggerType = " + triggerType);
                return;
            }

            if (mStatus.compareAndSet(STATUS_CONNECTED, STATUS_CONNECTED)) {
                handlerUserSpaceCallback(triggerType, CallbackTaskMessage.MSG_TYPE_CONNECTED);
                L.print("return when connected , triggerType = " + triggerType);
                return;
            }

            if (mDevice == null) {
                handlerUserSpaceCallback(triggerType, CallbackTaskMessage.MSG_TYPE_HOST_NULL);
                L.print("mDevice = null , triggerType = " + triggerType);
                return;
            }

            L.print("connecting.....................");
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
                            L.d("user启动连接成功");
                            InnerMessageHelper.sendConnectSuccByUserMessage(mDevice.id);
                            break;
                        case TRIGGER_FROM_SERVICE:
                            L.d("service启动连接成功");
                            break;
                        case TRIGGER_FROM_DISCONNECT_RETRY:
                            L.d("断线重连成功");
                            break;
                        case TRIGGER_FROM_TIMER_DETECTION:
                            L.d("定时重连成功");
                            break;
                    }
                } else {
                    mStatus.getAndSet(STATUS_NONE);
                    switch (triggerType) {
                        case TRIGGER_FROM_USER:
                            InnerMessageHelper.sendErrorCallbackMessage(CallbackTaskMessage.MSG_TYPE_CONNECT_FAIL);
                            L.d("user启动连接失败，启动断线重连");
                            mWatchdog.retry();
                            break;
                        case TRIGGER_FROM_SERVICE:
                            L.d("service启动连接失败，启动断线重连");
                            mWatchdog.retry();
                            break;
                        case TRIGGER_FROM_DISCONNECT_RETRY:
                            L.d("断线重连失败");
                            mWatchdog.retry();
                            break;
                        case TRIGGER_FROM_TIMER_DETECTION:
                            L.d("定时重连失败");
                            break;
                    }
                    Throwable throwable = channelFuture.cause();
                    if (throwable != null) {
                        L.d(throwable.toString());
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
                new ProtobufEncoder(),
                new ProtobufDecoder(),
                new MessageRecvHandler()
        };
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
    }
}
