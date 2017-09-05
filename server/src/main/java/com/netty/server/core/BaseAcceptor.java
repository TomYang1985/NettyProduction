package com.netty.server.core;

import java.util.concurrent.ThreadFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.internal.PlatformDependent;

/**
 * Created by robincxiao on 2017/8/23.
 */

public abstract class BaseAcceptor implements Acceptor {
    public static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();
    protected volatile ByteBufAllocator allocator;
    private ServerBootstrap bootstrap;
    private EventLoopGroup worker;
    private int workerNum;
    protected int currentPort;

    public BaseAcceptor(int port) {
        this.currentPort = port;
        this.workerNum = AVAILABLE_PROCESSORS << 1;

        initInner();
    }

    protected abstract void bind();

    protected ServerBootstrap bootstrap() {
        return bootstrap;
    }

    public int getCurrentPort() {
        return currentPort;
    }

    protected void initInner() {
        ThreadFactory workerFactory = new DefaultThreadFactory("netty.server");
        worker = new NioEventLoopGroup(workerNum, workerFactory);

        //使用池化的directBuffer
        /**
         * 一般高性能的场景下,使用的堆外内存，也就是直接内存，使用堆外内存的好处就是减少内存的拷贝，和上下文的切换，缺点是
         * 堆外内存处理的不好容易发生堆外内存OOM
         * 当然也要看当前的JVM是否只是使用堆外内存，换而言之就是是否能够获取到Unsafe对象#PlatformDependent.directBufferPreferred()
         */
        allocator = new PooledByteBufAllocator(PlatformDependent.directBufferPreferred());
        bootstrap = new ServerBootstrap().group(worker)
                .childOption(ChannelOption.ALLOCATOR, allocator)
                .option(ChannelOption.SO_BACKLOG, 32768)
                .option(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.ALLOW_HALF_CLOSURE, false);
    }

    @Override
    public void start() {
        bind();
    }

    public void shutdownGracefully() {
        worker.shutdownGracefully();
    }
}
