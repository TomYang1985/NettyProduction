package com.netty.server.nettymulticast;


import com.netty.server.utils.L;

import java.net.InetSocketAddress;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;

@Sharable
public class MulticastIdleStateTrigger extends ChannelInboundHandlerAdapter {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                L.d("MulticastIdleStateTrigger WRITER_IDLE");
//                DeviceProto.Device device = DeviceProto.Device.newBuilder()
//                        .setName("test")
//                        .setAddress("192.168.1.110").build();


                DatagramPacket packet = new DatagramPacket(Unpooled.copiedBuffer("QOTM?", CharsetUtil.UTF_8),
                        MulticastServer.groupAddress);

                ctx.writeAndFlush(packet);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
