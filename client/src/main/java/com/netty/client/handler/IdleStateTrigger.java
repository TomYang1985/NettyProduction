package com.netty.client.handler;

import com.netty.client.core.threadpool.ExecutorFactory;
import com.netty.client.core.threadpool.MessageSendTask;
import com.netty.client.msg.Header;
import com.netty.client.msg.SendMessage;
import com.netty.client.utils.L;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

@Sharable
public class IdleStateTrigger extends ChannelInboundHandlerAdapter {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                ExecutorFactory.submitSendTask(new MessageSendTask(ctx.channel(), new SendMessage(Header.MsgType.PING)));
            } else if (state == IdleState.READER_IDLE) {
                L.print("server " + ctx.channel().remoteAddress() + " lose");
                ctx.channel().close();//server失联，关闭channel，一定要关闭连接，否则userEventTriggered会被不停调用
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
