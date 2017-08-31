package com.netty.client.nettymulticast;

import com.netty.client.utils.L;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.NetUtil;
import io.netty.util.internal.PlatformDependent;

/**
 * Created by robincxiao on 2017/8/28.
 */

public class MulticastClient {
    public static final String DEAULT_HOST = "239.255.255.255";
    public static final int DEAULT_PORT = 30000;
    private ChannelFuture mChannelFuture;
    private InetSocketAddress mGroupAddress;
    private SocketAddress localAddr;
    protected volatile ByteBufAllocator allocator;
    private Bootstrap bootstrap;

    public void bind() {

        EventLoopGroup workers = new NioEventLoopGroup();
        mGroupAddress = new InetSocketAddress(DEAULT_HOST, DEAULT_PORT);
        localAddr = newSocketAddress();
        allocator = new PooledByteBufAllocator(PlatformDependent.directBufferPreferred());

        bootstrap = new Bootstrap().group(workers)
                .channel(NioDatagramChannel.class)
                .localAddress(DEAULT_PORT)
                .option(ChannelOption.ALLOCATOR, allocator)
                .option(ChannelOption.IP_MULTICAST_IF, NetUtil.LOOPBACK_IF)
                .option(ChannelOption.SO_REUSEADDR, true)
                .handler(new MulticastHandler());

        mChannelFuture = bootstrap.bind().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if(channelFuture.isSuccess()){
                    L.print("ScanDevice bind succ");
                    ((NioDatagramChannel) mChannelFuture.channel()).joinGroup(mGroupAddress, NetUtil.LOOPBACK_IF)
                            .addListener(new ChannelFutureListener() {
                                @Override
                                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                                    if(channelFuture.isSuccess()){
                                        L.print("joinGroup succ");
                                    }else {
                                        L.print("joinGroup fail");
                                    }
                                }
                            });
                }else {
                    L.print("ScanDevice bind succ");
                }
            }
        });



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
