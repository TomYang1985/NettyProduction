package com.netty.server.handler;

import com.netty.server.utils.L;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * Created by robincxiao on 2017/8/23.
 * 心跳管理
 */
@ChannelHandler.Sharable
public class IdleStateTrigger extends ChannelInboundHandlerAdapter {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                L.print("client " + ctx.channel().remoteAddress() + " lose");
                ctx.channel().close();//client失联，关闭channel
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
