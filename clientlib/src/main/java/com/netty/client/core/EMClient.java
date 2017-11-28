package com.netty.client.core;

import android.content.Context;

import com.netty.client.codec.KeyManager;
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
import java.util.concurrent.locks.ReentrantLock;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * Created by robincxiao on 2017/8/24.
 */

public class EMClient extends BaseConnector implements ChannelHandlerHolder {
    //private static final String DEAULT_HOST = "192.168.1.112";
    private static final int DEAULT_PORT = 9987;
    private static final int MAX_TRY_COUNT = 2;//最大port尝试次数
    private static final int STATUS_NONE = 1;
    private static final int STATUS_CONNECTING = 2;
    private static final int STATUS_CONNECTED = 3;
    private static final long WRITER_IDLE_TIME = 10;
    private static final long READER_IDLE_TIME = 20;
    /*******触发连接的因素********/
    private static final int TRIGGER_FROM_USER = 1;//用户主动启动连接
    private static final int TRIGGER_FROM_DISCONNECT_RETRY = 2;//断线重连
    private static final int TRIGGER_FROM_TIMER_DETECTION = 3;//定时检测
    private static final int TRIGGER_FROM_KEY_EXCHANGE_EXCEPTION = 4;//未收到服务端交换密钥的响应(可能的原因是服务端端口被占用，连接上了非我们自己的服务器)
    private static final int TRIGGER_FROM_USER_RETRY = 5;//由TRIGGER_FROM_USER触发的连接失败，进而在进行端口号递增重连

    private volatile static EMClient sInstance;
    private Context mContext;
    private ChannelFuture mFuture;
    private ConnectionWatchdog mWatchdog;
    private AtomicInteger mStatus;
    private EMDevice mDevice;//当前连接设备
    private volatile int currentPort = DEAULT_PORT;
    private ReentrantLock mainLock = new ReentrantLock();
    private boolean isInited = false;

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

    public void connectDevice(String host){
        connectDevice(host, "");
    }

    public void connectDevice(String host, String name) {
        EMDevice newDevice = new EMDevice(host, name);

        if (mDevice == null) {
            this.mDevice = newDevice;
            connect(TRIGGER_FROM_USER);
        } else {
            //如果连接的新设备与以前的设备相同，则直接连接
            if (mDevice.id.equals(newDevice.id)) {
                connect(TRIGGER_FROM_USER);
            } else {
                /**
                 * 如果切换设备，先关闭Watchdog的功能，因为此时不需要断线重连和定时功能；然后关闭当前连接；最后再重新初始化，建立新的连接
                 */
                mWatchdog.disableWatchdog();
                shutdownGracefully();
                initInner();
                this.mDevice = newDevice;
                resetCurrentPort();//切换设备需要复位为初始端口
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
        mainLock.lock();
        try {
            if(isInited){
                return;
            }
            isInited = true;
        }finally {
            mainLock.unlock();
        }

        mContext = context.getApplicationContext();

        L.init();//log的初始化，需要放在mContext的初始化后面

        HttpServer.getInstance().start();//启动http server
        mStatus = new AtomicInteger(STATUS_NONE);

        mWatchdog = new ConnectionWatchdog(context);
        mWatchdog.setListener(new ConnectionWatchdog.WatchdogListener() {
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

            @Override
            public void checkKeyExchange() {
                int keyExchangeStatus = KeyManager.getInstance().getKeyExchangeStatus();

                L.writeFile("keyExchangeStatus=" + keyExchangeStatus);
                if(keyExchangeStatus == KeyManager.KEY_EXCHANGE_NULL) {//未收到服务端交换密钥的响应，则应该修改port
                    if(currentPort < DEAULT_PORT + MAX_TRY_COUNT) {
                        /**
                         * 连接的是非法的服务设备，先关闭Watchdog的功能，因为此时不需要断线重连和定时功能功能；
                         * 然后关闭当前连接；最后再重新初始化，建立新的连接
                         */
                        mWatchdog.disableWatchdog();
                        shutdownGracefully();
                        initInner();
                        connect(TRIGGER_FROM_KEY_EXCHANGE_EXCEPTION);
                    }else {
                        resetCurrentPort();
                    }
                }
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

    private void resetCurrentPort(){
        mainLock.lock();
        try {
            currentPort = DEAULT_PORT;
        }finally {
            mainLock.unlock();
        }
    }

    private void connect(final int triggerType) {
//        if (mContext == null) {
//            mWatchdog.reset();//原因同下
//            L.writeFile("return mContext == null , triggerType = " + triggerType);
//            return;
//        }
//
//        if (!NetUtils.isWifi(mContext)) {
//            /**
//             * 当wifi断开，进行断线重连过程时，此处会直接返回，导致不会执行disconnectRetry中mCounter >= MAX_RETRY_NUM
//             * 去设置mTimerEnable的状态标记，所以此处需要复位
//             */
//            mWatchdog.reset();
//            handlerUserSpaceCallback(triggerType, CallbackMessage.MSG_TYPE_NOT_WIFI);
//            L.writeFile("return when net is not wifi , triggerType = " + triggerType);
//            return;
//        }

        mainLock.lock();
        try {
            /**
             * 如下两种触发重连过程，进行端口号递增重连
             * 1.未收到服务端交换密钥的响应，则应该修改port，重新进行连接
             * 2.TRIGGER_FROM_USER_RETRY触发的重连
             */
            if(triggerType == TRIGGER_FROM_KEY_EXCHANGE_EXCEPTION || triggerType == TRIGGER_FROM_USER_RETRY) {
                currentPort++;
            }else {
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
            }

            if (mDevice == null) {
                handlerUserSpaceCallback(triggerType, CallbackMessage.MSG_TYPE_HOST_NULL);
                L.writeFile("mDevice = null , triggerType = " + triggerType);
                return;
            }

            L.writeFile("EMClient connecting.....................");
            mStatus.set(STATUS_CONNECTING);

            mFuture = bootstrap().connect(mDevice.id, currentPort);
        }finally {
            mainLock.unlock();
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
                            L.writeFile("user启动连接成功" + currentPort);
                            //InnerMessageHelper.sendConnectSuccByUserMessage(mDevice.id);
                            break;
                        case TRIGGER_FROM_DISCONNECT_RETRY:
                            L.writeFile("断线重连成功" + currentPort);
                            break;
                        case TRIGGER_FROM_TIMER_DETECTION:
                            L.writeFile("定时重连成功" + currentPort);
                            break;
                        case TRIGGER_FROM_KEY_EXCHANGE_EXCEPTION:
                            L.writeFile("key exchange connect succ port = " + currentPort);
                            break;
                        case TRIGGER_FROM_USER_RETRY:
                            L.writeFile("user retry connect succ port = " + currentPort);
                            break;
                    }
                } else {
                    Throwable throwable = channelFuture.cause();
                    if (throwable != null) {
                        L.writeFile("connect fail cause :: " + throwable.toString());
                    }

                    //连接失败后必须要设置状态为STATUS_NONE
                    mStatus.getAndSet(STATUS_NONE);

                    switch (triggerType) {
                        case TRIGGER_FROM_USER:
                            //InnerMessageHelper.sendErrorCallbackMessage(CallbackMessage.MSG_TYPE_CONNECT_FAIL);
                            L.writeFile("user connect fail port =" + currentPort);
                            if(currentPort >= DEAULT_PORT + MAX_TRY_COUNT){
                                currentPort = DEAULT_PORT;
                            }else {
                                connect(TRIGGER_FROM_USER_RETRY);
                            }
                            break;
                        case TRIGGER_FROM_DISCONNECT_RETRY:
                            L.writeFile("断线重连失败" + currentPort);
                            mWatchdog.disconnectRetry();
                            break;
                        case TRIGGER_FROM_TIMER_DETECTION:
                            L.writeFile("定时重连失败" + currentPort);
                            break;
                        case TRIGGER_FROM_KEY_EXCHANGE_EXCEPTION:
                        case TRIGGER_FROM_USER_RETRY: {
                            switch (triggerType){
                                case TRIGGER_FROM_KEY_EXCHANGE_EXCEPTION:
                                    L.writeFile("key exchange connect fail port = " + currentPort);
                                    break;
                                case TRIGGER_FROM_USER_RETRY:
                                    L.writeFile("user retry connect fail port = " + currentPort);
                                    break;
                            }

                            if (currentPort >= DEAULT_PORT + MAX_TRY_COUNT) {
                                //如果连接的端口已经是最大端口且还未连接成功，需要通知上层，设备正在重连连接
                                InnerMessageHelper.sendInActiveCallbackMessage();
                                currentPort = DEAULT_PORT;
                                mWatchdog.enableWatchdog();//注意：如果进行3次连接后依然失败，记得使能Watchdog的断线重连和定时功能
                            } else {
                                connect(triggerType);
                            }
                            break;
                        }
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
        L.print("shutdownGracefully");
        mStatus.getAndSet(STATUS_NONE);
        super.shutdownGracefully();
    }

    public void onDestory() {
        L.print("onDestory");
        shutdownGracefully();
        ExecutorFactory.shutdownNow();
        mWatchdog.onDestory();
        HttpServer.getInstance().destory();
        sInstance = null;
    }
}
