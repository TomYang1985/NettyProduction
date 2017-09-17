package com.tencent.tvmanager.netty.core;

import android.content.Context;

import com.tencent.tvmanager.netty.codec.KeysManager;
import com.tencent.tvmanager.netty.codec.NettyDecoder;
import com.tencent.tvmanager.netty.codec.NettyEncoder;
import com.tencent.tvmanager.netty.common.Watchdog;
import com.tencent.tvmanager.netty.core.threadpool.ExecutorFactory;
import com.tencent.tvmanager.netty.handler.ConnectionManagerHandler;
import com.tencent.tvmanager.netty.handler.IdleStateTrigger;
import com.tencent.tvmanager.netty.handler.MessageRecvHandler;
import com.tencent.tvmanager.util.L;

import java.net.BindException;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * Created by robincxiao on 2017/8/23.
 */

public class EMAcceptor extends BaseAcceptor implements ChannelHandlerHolder {
    private static final int DEAULT_PORT = 9987;
    private static final long READER_IDLE_TIME = 20;
    private static final long MAX_REBIND_NUM = 3;
    private static final int STATUS_NONE = 1;
    private static final int STATUS_BINDING = 2;
    private static final int STATUS_BINDED = 3;
    private volatile static EMAcceptor sInstance;
    private Context mContext;
    private ChannelFuture mChannelFuture;
    private AtomicInteger mStatus;
    private Watchdog mWatchdog;
    private int mBindCounter = 0;

    private EMAcceptor() {
        super(DEAULT_PORT);
    }

    public static EMAcceptor getInstance() {
        if (sInstance == null) {
            synchronized (EMAcceptor.class) {
                if (sInstance == null) {
                    sInstance = new EMAcceptor();
                }
            }
        }

        return sInstance;
    }

    public Context getContext() {
        return mContext;
    }

    public void init(Context context) {
        mContext = context;
        mStatus = new AtomicInteger(STATUS_NONE);
        mWatchdog = new Watchdog(context);

        mWatchdog.setListener(new Watchdog.WatchdogListener() {
            @Override
            public void disconnectRetry() {

            }

            @Override
            public void timerCheck() {
                bind();
            }
        });

        bootstrap().channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(handlers());
                    }
                });
    }

    public EMMessageManager getEMMessageManager(){
        return EMMessageManager.getInstance();
    }

    public EMConnectManager getEMConnectManager(){
        return EMConnectManager.getInstance();
    }

    @Override
    protected void bind() {
        if (mStatus.compareAndSet(STATUS_BINDING, STATUS_BINDING) || mStatus.compareAndSet(STATUS_BINDED, STATUS_BINDED)) {
            L.print("return when binding or binded");
            return;
        }

        L.print("binding........");
        mStatus.getAndSet(STATUS_BINDING);

        try {
            synchronized (bootstrap()) {
                mChannelFuture = bootstrap().bind(new InetSocketAddress(currentPort));
            }

            if (mChannelFuture != null) {
                mChannelFuture.addListener(new BindListener());
            }
        } catch (Exception e) {
            mStatus.getAndSet(STATUS_NONE);
            e.printStackTrace();
        }
    }

    @Override
    public ChannelHandler[] handlers() {
        return new ChannelHandler[]{
                new IdleStateHandler(READER_IDLE_TIME, 0, 0, TimeUnit.SECONDS),
                new IdleStateTrigger(),
                new NettyEncoder(),
                new NettyDecoder(),
                new ConnectionManagerHandler(),
                new MessageRecvHandler()
        };
    }

    @Override
    public void shutdownGracefully() {
        mStatus.getAndSet(STATUS_NONE);
        super.shutdownGracefully();
    }

    public void onDestory(){
        shutdownGracefully();
        ExecutorFactory.shutdownNow();
        KeysManager.getInstance().onDestory();
        mWatchdog.onDestory();
    }

    private class BindListener implements ChannelFutureListener {
        @Override
        public void operationComplete(ChannelFuture channelFuture) throws Exception {
            if (channelFuture.isSuccess()) {
                mStatus.getAndSet(STATUS_BINDED);
                L.d("bind succ port = " + EMAcceptor.this.currentPort);
            } else {
                mStatus.getAndSet(STATUS_NONE);
                L.d("绑定失败");
                Throwable throwable = channelFuture.cause();
                if (throwable != null) {
                    if (throwable instanceof BindException) {//bind异常(bind failed: EADDRINUSE (Address already in use))
                        L.d("BindException Address already in use");
                        if (++EMAcceptor.this.currentPort < DEAULT_PORT + 3) {
                            L.d("rebinding port = " + EMAcceptor.this.currentPort);
                            bind();
                        }
                    } else {
                        L.print(throwable.toString());
                        //bind();
                    }
                }
            }
        }
    }
}
