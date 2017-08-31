package com.netty.server.nettymulticast;

import com.netty.server.utils.L;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

/**
 * Created by robincxiao on 2017/8/29.
 */

public class MulticastHandler extends SimpleChannelInboundHandler<DatagramPacket>{

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket packet) throws Exception {
        L.d("recv Multicast from ");
    }
}
