package com.netty.server.nettymulticast;

import com.netty.server.utils.L;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.NetUtil;
import io.netty.util.internal.PlatformDependent;

/**
 * Created by robincxiao on 2017/8/28.
 */

public class MulticastServer {
    public static final String DEAULT_HOST = "239.255.255.255";
    public static final int DEAULT_PORT = 30000;
    private static final long WRITER_IDLE_TIME = 5;
    private ChannelFuture mChannelFuture;
    public static InetSocketAddress groupAddress;
    private SocketAddress localAddr;
    protected volatile ByteBufAllocator allocator;

    public void bind() {
        EventLoopGroup workers = new NioEventLoopGroup();

        localAddr = newSocketAddress();
        groupAddress = new InetSocketAddress(DEAULT_HOST, DEAULT_PORT);
        allocator = new PooledByteBufAllocator(PlatformDependent.directBufferPreferred());

        Bootstrap bootstrap = new Bootstrap().group(workers)
                .channel(NioDatagramChannel.class)
                .localAddress(localAddr)
                .option(ChannelOption.ALLOCATOR, allocator)
                .option(ChannelOption.IP_MULTICAST_IF, NetUtil.LOOPBACK_IF)
                .option(ChannelOption.SO_REUSEADDR, true)
                .handler(new ChannelInitializer<NioDatagramChannel>() {
                    @Override
                    protected void initChannel(NioDatagramChannel nioDatagramChannel) throws Exception {
                        nioDatagramChannel.pipeline().addLast(new IdleStateHandler(0, WRITER_IDLE_TIME, 0, TimeUnit.SECONDS));
                        nioDatagramChannel.pipeline().addLast(new MulticastIdleStateTrigger());
                        //nioDatagramChannel.pipeline().addLast(new MulticastHandler());
                    }
                });
        mChannelFuture = bootstrap.bind(groupAddress.getPort()).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if(channelFuture.isSuccess()){
                    InetSocketAddress addr = (InetSocketAddress) channelFuture.channel().localAddress();
                    L.print("MulticastServer bind succ port = " + addr.getPort());
                }else {
                    L.print("MulticastServer bind fail");
                    if(channelFuture.cause() != null){
                        L.print(channelFuture.cause().toString());
                    }
                }
            }
        });

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (true) {
//                    try {
//                        Thread.sleep(5000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    L.print("send data");
//                    mChannelFuture.channel().writeAndFlush(new DatagramPacket(Unpooled.copyInt(1), groupAddress));
//                }
//            }
//        }).start();
    }

    protected SocketAddress newSocketAddress() {
        // We use LOCALHOST4 as we use InternetProtocolFamily.IPv4 when creating the DatagramChannel and its
        // not supported to bind to and IPV6 address in this case.
        //
        // See also http://hg.openjdk.java.net/jdk8u/jdk8u/jdk/file/e74259b3eadc/
        // src/share/classes/sun/nio/ch/DatagramChannelImpl.java#l684
        return new InetSocketAddress(NetUtil.LOCALHOST4, DEAULT_PORT);
    }

}
