package com.netty.client.handler;

import com.netty.client.utils.L;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by robincxiao on 2017/8/23.
 * 连接和异常管理
 */
@ChannelHandler.Sharable
public class ConnectionManagerHandler extends ChannelDuplexHandler {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        L.print("ConnectionManagerHandler_channelActive_" + ctx.channel().localAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        L.print("ConnectionManagerHandler_channelInactive_" + ctx.channel().localAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        L.print("exceptionCaught");
        L.d(cause.toString());
    }
}
