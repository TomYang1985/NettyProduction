package com.netty.client.nettymulticast;

import com.netty.client.utils.L;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

/**
 * Created by robincxiao on 2017/8/29.
 */

public class MulticastHandler extends SimpleChannelInboundHandler<DatagramPacket>{

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket o) throws Exception {
        L.d("recv Multicast from ");
    }
}

//public class MulticastHandler extends SimpleChannelInboundHandler<MessageLite>{
//
//    @Override
//    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MessageLite messageLite) throws Exception {
//        DeviceProto.Device device = (DeviceProto.Device)messageLite;
//        L.d("recv Multicast from " + device.getName() + device.getAddress());
//    }
//}
