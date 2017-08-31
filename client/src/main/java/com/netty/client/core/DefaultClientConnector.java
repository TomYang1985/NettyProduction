package com.netty.client.core;

import android.content.Context;

import com.netty.client.codec.ProtobufDecoder;
import com.netty.client.codec.ProtobufEncoder;
import com.netty.client.common.ConnectionWatchdog;
import com.netty.client.core.threadpool.MessageRecvExecutor;
import com.netty.client.core.threadpool.MessageSendExecutor;
import com.netty.client.handler.ConnectionManagerHandler;
import com.netty.client.handler.IdleStateTrigger;
import com.netty.client.handler.MessageRecvHandler;
import com.netty.client.utils.L;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * Created by robincxiao on 2017/8/24.
 */

public class DefaultClientConnector extends NettyClientConnector implements ChannelHandlerHolder {
    private static final String DEAULT_HOST = "192.168.1.112";
    private static final int DEAULT_PORT = 9987;
    private static final int STATUS_NONE = 1;
    private static final int STATUS_CONNECTING = 2;
    private static final int STATUS_CONNECTED = 3;
    private static final long WRITER_IDLE_TIME = 5;
    private static final long READER_IDLE_TIME = 12;
    private static final int TRIGGER_FROM_DIRECT = 1;//直连启动
    private static final int TRIGGER_FROM_DISCONNECT_RETRY = 2;//断线重连
    private static final int TRIGGER_FROM_TIMER_DETECTION = 3;//定时检测
    private ConnectionWatchdog mWatchdog;
    private AtomicInteger mStatus;

    public DefaultClientConnector() {
        super();
    }

    public void init(Context context) {
        mStatus = new AtomicInteger(STATUS_NONE);

        mWatchdog = new ConnectionWatchdog(context);
        mWatchdog.setListener(new ConnectionWatchdog.WatchdogListener() {
            @Override
            public void disconnectRetry() {
                mStatus.getAndSet(STATUS_NONE);//断线重连需要重新设置状态
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
        return mStatus.compareAndSet(STATUS_CONNECTED, STATUS_CONNECTED);
    }

    @Override
    public void connect() {
        connect(TRIGGER_FROM_DIRECT);
    }

    public void connect(final int triggerType) {
        if (mStatus.compareAndSet(STATUS_CONNECTING, STATUS_CONNECTING) || mStatus.compareAndSet(STATUS_CONNECTED, STATUS_CONNECTED)) {
            L.print("return when connecting or connected , triggerType = " + triggerType);
            return;
        }

        L.print("connecting.....................");
        mStatus.getAndSet(STATUS_CONNECTING);
        ChannelFuture future = null;

        synchronized (bootstrap()) {
            bootstrap().handler(new ChannelInitializer<Channel>() {

                @Override
                protected void initChannel(Channel ch) throws Exception {
                    ch.pipeline().addLast(handlers());
                }
            });
            future = bootstrap().connect(DEAULT_HOST, DEAULT_PORT);
        }

        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    mStatus.getAndSet(STATUS_CONNECTED);
                    switch (triggerType) {
                        case TRIGGER_FROM_DIRECT:
                            L.d("直连成功");
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
                        case TRIGGER_FROM_DIRECT:
                            L.d("直连失败，启动断线重连");
                            channelFuture.channel().pipeline().fireChannelInactive();
                            break;
                        case TRIGGER_FROM_DISCONNECT_RETRY:
                            L.d("断线重连失败");
                            channelFuture.channel().pipeline().fireChannelInactive();
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

    public void onDestory(){
        mStatus.getAndSet(STATUS_NONE);
        shutdownGracefully();
        MessageRecvExecutor.shutdownNow();
        MessageSendExecutor.shutdownNow();
        mWatchdog.onDestory();
    }
}
