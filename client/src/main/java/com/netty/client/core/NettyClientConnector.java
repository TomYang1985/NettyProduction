package com.netty.client.core;

import java.util.concurrent.ThreadFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultMessageSizeEstimator;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by robincxiao on 2017/8/24.
 */

public abstract class NettyClientConnector implements ClientConnector {
    public static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();
    private Bootstrap bootstrap;
    private EventLoopGroup worker;
    private int nWorkers;
    protected volatile ByteBufAllocator allocator;

    public NettyClientConnector() {
        nWorkers = AVAILABLE_PROCESSORS << 1;

        initInner();
    }

    protected void initInner() {
        ThreadFactory workerFactory = new DefaultThreadFactory("client.connector");
        worker = new NioEventLoopGroup(nWorkers, workerFactory);

        bootstrap = new Bootstrap().group(worker).
                option(ChannelOption.ALLOCATOR, allocator)
                .option(ChannelOption.MESSAGE_SIZE_ESTIMATOR, DefaultMessageSizeEstimator.DEFAULT)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) SECONDS.toMillis(5))
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOW_HALF_CLOSURE, false)
                .channel(NioSocketChannel.class);
    }

    protected Bootstrap bootstrap() {
        return bootstrap;
    }

    public void shutdownGracefully() {
        worker.shutdownGracefully();
    }
}
