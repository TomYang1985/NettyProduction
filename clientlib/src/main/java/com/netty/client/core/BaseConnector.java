package com.netty.client.core;

import java.util.concurrent.ThreadFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultMessageSizeEstimator;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.internal.PlatformDependent;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by robincxiao on 2017/8/24.
 */

public abstract class BaseConnector implements Connector {
    public static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();
    private Bootstrap bootstrap;
    private EventLoopGroup worker;
    private int nWorkers;
    protected volatile ByteBufAllocator allocator;

    protected BaseConnector() {
        nWorkers = AVAILABLE_PROCESSORS << 1;
        //使用池化的directBuffer
        /**
         * 一般高性能的场景下,使用的堆外内存，也就是直接内存，使用堆外内存的好处就是减少内存的拷贝，和上下文的切换，缺点是
         * 堆外内存处理的不好容易发生堆外内存OOM
         * 当然也要看当前的JVM是否只是使用堆外内存，换而言之就是是否能够获取到Unsafe对象#PlatformDependent.directBufferPreferred()
         */
        allocator = new PooledByteBufAllocator(PlatformDependent.directBufferPreferred());
        initInner();
    }

    /**
     * 在切换连接设备的过程中会被多次调用
     */
    protected void initInner() {
        ThreadFactory workerFactory = new DefaultThreadFactory("client.connector");
        worker = new NioEventLoopGroup(nWorkers, workerFactory);

        bootstrap = new Bootstrap()
                .option(ChannelOption.ALLOCATOR, allocator)
                .option(ChannelOption.MESSAGE_SIZE_ESTIMATOR, DefaultMessageSizeEstimator.DEFAULT)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) SECONDS.toMillis(5))
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOW_HALF_CLOSURE, false)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(handlers());
                    }
                }).group(worker);
    }

    protected Bootstrap bootstrap() {
        return bootstrap;
    }

    protected void shutdownGracefully() {
        worker.shutdownGracefully();
    }

    protected abstract ChannelHandler[] handlers();
}
