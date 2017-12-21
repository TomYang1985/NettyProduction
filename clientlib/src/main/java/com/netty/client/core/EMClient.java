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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.logging.InternalLogLevel;

/**
 * Created by robincxiao on 2017/8/24.
 */

public class EMClient extends BaseConnector implements ChannelHandlerHolder {
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
    private static final int TRIGGER_FROM_WIFI_CONNECTED = 5;//监听到WIFI连接成功，进行连接
    private static final int TRIGGER_FROM_CHANGE_DEVICE = 6;//切换设备连接

    private volatile static EMClient sInstance;
    private Context mContext;
    private volatile ChannelFuture mFuture;
    private ConnectionWatchdog mWatchdog;
    private AtomicInteger mStatus;
    private EMDevice mDevice;//当前连接设备
    private volatile int mCurrentPort = DEAULT_PORT;
    private ReentrantLock mainLock = new ReentrantLock();
    private boolean isInited = false;
    private ExecutorService mThreadPoolExecutor;

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

    public void connectDevice(String host) {
        connectDevice(host, "");
    }

    public void connectDevice(String host, String name) {
        if (!NetUtils.isWifi(mContext)) {
            handlerUserSpaceCallback(TRIGGER_FROM_USER, CallbackMessage.MSG_TYPE_NOT_CONNECT_WIFI);
            return;
        }

        EMDevice newDevice = new EMDevice(host, name);

        if (mDevice == null) {
            this.mDevice = newDevice;
            submit(TRIGGER_FROM_USER);
        } else {
            //如果连接的新设备与以前的设备相同，则直接连接
            if (mDevice.id.equals(newDevice.id)) {
                submit(TRIGGER_FROM_USER);
            } else {
                submit(TRIGGER_FROM_CHANGE_DEVICE, newDevice);
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
            if (isInited) {
                return;
            }
            isInited = true;
        } finally {
            mainLock.unlock();
        }

        mContext = context.getApplicationContext();

        mThreadPoolExecutor = Executors.newSingleThreadExecutor();

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
                submit(TRIGGER_FROM_DISCONNECT_RETRY);
            }

            @Override
            public void timerCheck() {
                submit(TRIGGER_FROM_TIMER_DETECTION);
            }

            @Override
            public void unValidationServer() {
                submit(TRIGGER_FROM_KEY_EXCHANGE_EXCEPTION);
            }

            @Override
            public void wifiEnabled() {
                submit(TRIGGER_FROM_WIFI_CONNECTED);
            }
        });
    }

    private void submit(int triggerType) {
        mThreadPoolExecutor.submit(new Worker(triggerType));
    }

    private void submit(int triggerType, EMDevice newDevice) {
        mThreadPoolExecutor.submit(new Worker(triggerType, newDevice));
    }

    private final class Worker implements Runnable {
        private int triggerType;
        private EMDevice newDevice;

        public Worker(int triggerType) {
            this.triggerType = triggerType;
        }

        public Worker(int triggerType, EMDevice newDevice) {
            this.triggerType = triggerType;
            this.newDevice = newDevice;
        }

        @Override
        public void run() {
            if (triggerType != TRIGGER_FROM_CHANGE_DEVICE) {
                if (mStatus.compareAndSet(STATUS_CONNECTING, STATUS_CONNECTING)) {
                    handlerUserSpaceCallback(triggerType, CallbackMessage.MSG_TYPE_CONNECTING);
                    L.writeFile("return when connecting , triggerType = " + triggerType);
                    return;
                }

                if (triggerType != TRIGGER_FROM_KEY_EXCHANGE_EXCEPTION) {
                    if (mStatus.compareAndSet(STATUS_CONNECTED, STATUS_CONNECTED)) {
                        handlerUserSpaceCallback(triggerType, CallbackMessage.MSG_TYPE_CONNECTED);
                        L.writeFile("return when connected , triggerType = " + triggerType);
                        return;
                    }
                }
            }

            if (mDevice == null) {
                handlerUserSpaceCallback(triggerType, CallbackMessage.MSG_TYPE_HOST_NULL);
                L.writeFile("mDevice = null , triggerType = " + triggerType);
                return;
            }

            L.writeFile("EMClient connecting..................." + triggerType);
            mStatus.set(STATUS_CONNECTING);

            if (triggerType == TRIGGER_FROM_USER || triggerType == TRIGGER_FROM_CHANGE_DEVICE) {
                if (triggerType == TRIGGER_FROM_CHANGE_DEVICE) {//切换设备
                    //如果切换设备，先关闭Watchdog的功能，因为此时不需要断线重连、定时功能、wifi连接监听；然后关闭当前连接；最后再重新初始化，建立新的连接
                    mWatchdog.disableWatchdog();
                    shutdownGracefully();
                    initInner();
                    mDevice = newDevice;
                    mCurrentPort = DEAULT_PORT;//切换设备需要复位为初始端口
                }

                do {
                    try {
                        mFuture = bootstrap().connect(mDevice.id, mCurrentPort).sync().addListener(new GenericFutureListener<Future<? super Void>>() {
                            @Override
                            public void operationComplete(Future<? super Void> future) throws Exception {
                                L.writeFile("operationComplete.............");
                            }
                        });
                    } catch (Exception e) {
                        mFuture = null;
                        L.writeFile(e.toString());
                        e.printStackTrace();
                    }

                    if (mFuture != null && mFuture.isSuccess()) {
                        L.writeFile("user connect succ port = " + mCurrentPort);
                        mStatus.set(STATUS_CONNECTED);
                        break;//连接成功跳出循环
                    } else {
                        printCause();
                        L.writeFile("user connect fail port = " + mCurrentPort);
                        if (mCurrentPort >= DEAULT_PORT + MAX_TRY_COUNT) {
                            //如果连接的端口已经是最大端口且还未连接成功，需要通知上层，设备连接失败
                            InnerMessageHelper.sendErrorCallbackMessage(CallbackMessage.MSG_TYPE_CONNECT_FAIL);
                            mCurrentPort = DEAULT_PORT;
                            mStatus.set(STATUS_NONE);
                            mWatchdog.enableWatchdog();
                            break;
                        }
                    }
                } while (++mCurrentPort <= DEAULT_PORT + MAX_TRY_COUNT);
            } else if (triggerType == TRIGGER_FROM_KEY_EXCHANGE_EXCEPTION) {
                if (mCurrentPort >= DEAULT_PORT + MAX_TRY_COUNT) {
                    //如果连接的端口已经是最大端口且还未连接成功，需要通知上层，设备连接失败
                    InnerMessageHelper.sendErrorCallbackMessage(CallbackMessage.MSG_TYPE_CONNECT_FAIL);
                    mCurrentPort = DEAULT_PORT;
                    mStatus.set(STATUS_NONE);
                } else {
                    //端口递增连接，先关闭Watchdog的功能，因为此时不需要断线重连、定时功能、wifi连接监听；然后关闭当前连接；最后再重新初始化，建立新的连接
                    mWatchdog.disableWatchdog();
                    //先断开连接然后再连接
                    shutdownGracefully();
                    initInner();

                    while (++mCurrentPort <= DEAULT_PORT + MAX_TRY_COUNT) {
                        try {
                            mFuture = bootstrap().connect(mDevice.id, mCurrentPort).sync();
                        } catch (Exception e) {
                            mFuture = null;
                            L.writeFile(e.toString());
                            e.printStackTrace();
                        }

                        if (mFuture != null && mFuture.isSuccess()) {
                            L.writeFile("key exchange connect succ port = " + mCurrentPort);
                            mStatus.set(STATUS_CONNECTED);
                            break;//连接成功跳出循环
                        } else {
                            printCause();
                            L.writeFile("key exchange connect fail port = " + mCurrentPort);
                            if (mCurrentPort >= DEAULT_PORT + MAX_TRY_COUNT) {
                                //如果连接的端口已经是最大端口且还未连接成功，需要通知上层，设备连接失败
                                InnerMessageHelper.sendErrorCallbackMessage(CallbackMessage.MSG_TYPE_CONNECT_FAIL);
                                mCurrentPort = DEAULT_PORT;
                                mStatus.set(STATUS_NONE);
                                mWatchdog.enableWatchdog();
                                break;
                            }
                        }
                    }
                }
            } else if (triggerType == TRIGGER_FROM_DISCONNECT_RETRY) {
                try {
                    mFuture = bootstrap().connect(mDevice.id, mCurrentPort).sync();
                } catch (Exception e) {
                    mFuture = null;
                    L.writeFile(e.toString());
                    e.printStackTrace();
                }

                if (mFuture != null && mFuture.isSuccess()) {
                    L.writeFile("断线重连成功" + mCurrentPort);
                    mStatus.set(STATUS_CONNECTED);
                } else {
                    L.writeFile("断线重连失败" + mCurrentPort);
                    mStatus.getAndSet(STATUS_NONE);
                    mWatchdog.disconnectRetry();
                }
            } else {
                try {
                    mFuture = bootstrap().connect(mDevice.id, mCurrentPort).sync().await();
                } catch (Exception e) {
                    mFuture = null;
                    L.writeFile(e.toString());
                    e.printStackTrace();
                }
                if (mFuture != null && mFuture.isSuccess()) {
                    switch (triggerType) {
                        case TRIGGER_FROM_TIMER_DETECTION:
                            L.writeFile("定时重连成功" + mCurrentPort);
                            break;
                        case TRIGGER_FROM_WIFI_CONNECTED:
                            L.writeFile("wifi connected connect succ port = " + mCurrentPort);
                            break;
                    }

                    mStatus.getAndSet(STATUS_CONNECTED);
                } else {
                    switch (triggerType) {
                        case TRIGGER_FROM_TIMER_DETECTION:
                            mStatus.getAndSet(STATUS_NONE);
                            L.writeFile("定时重连失败" + mCurrentPort);
                            break;
                        case TRIGGER_FROM_WIFI_CONNECTED:
                            mStatus.getAndSet(STATUS_NONE);
                            L.writeFile("wifi connected connect fail port = " + mCurrentPort);
                            break;
                    }
                }
            }
        }

        private void printCause() {
            if (mFuture != null) {
                Throwable throwable = mFuture.cause();
                if (throwable != null) {
                    L.writeFile("connect fail cause :: " + throwable.toString());
                }
            }
        }

        private void printCause(Throwable throwable) {
            if (throwable != null) {
                L.writeFile(throwable.toString());
            }
        }
    }

    /**
     * 处理用户空间连接回调
     *
     * @param type
     */
    private void handlerUserSpaceCallback(final int triggerType, int type) {
        if (triggerType == TRIGGER_FROM_USER) {
            InnerMessageHelper.sendErrorCallbackMessage(type);
        }
    }

    @Override
    public ChannelHandler[] handlers() {
        return new ChannelHandler[]{
                new IdleStateHandler(READER_IDLE_TIME, WRITER_IDLE_TIME, 0, TimeUnit.SECONDS),
                mWatchdog,
                //new IdleStateTrigger(),
                new ConnectionManagerHandler(),
                new NettyEncoder(),
                new NettyDecoder(),
                new StatisticsSendHandler(),
                new StatisticsRecvHandler(),
                new MessageRecvHandler()
        };
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
        if (isActive() && mFuture.channel().localAddress() != null) {
            return HostUtils.parseHost(mFuture.channel().localAddress().toString());
        }

        return "";
    }

    /**
     * 获取server host(已连接状态时)
     *
     * @return
     */
    public String remoteHost() {
        if (isActive()) {
            SocketAddress address = mFuture.channel().remoteAddress();
            if (address != null) {
                return HostUtils.parseHost(address.toString());
            }

        }
        return "";
    }

    /**
     * 获取本地文件url
     *
     * @param path 本地文件路径
     * @return
     */
    public String getLocalUrl(String path) {
        if (mFuture != null && mFuture.channel() != null && mFuture.channel().localAddress() != null) {
            return HttpServer.getInstance().getLocalUrl(localHost(), path);
        }

        return "";
    }

    @Override
    protected void shutdownGracefully() {
        L.print("shutdownGracefully");
        mStatus.getAndSet(STATUS_NONE);
        super.shutdownGracefully();
    }

    public void onDestory() {
        L.print("onDestory");
        shutdownGracefully();
        mThreadPoolExecutor.shutdownNow();
        ExecutorFactory.shutdownNow();
        mWatchdog.onDestory();
        HttpServer.getInstance().destory();
        sInstance = null;
    }
}
