package com.netty.server.handler;

import com.netty.server.core.threadpool.ExecutorFactory;
import com.netty.server.core.threadpool.MessageRecvTask;
import com.netty.server.core.threadpool.MessageSendTask;
import com.netty.server.msg.Header;
import com.netty.server.msg.RecvMsg;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by robincxiao on 2017/8/23.
 */
@ChannelHandler.Sharable
public class MessageRecvHandler extends SimpleChannelInboundHandler<RecvMsg> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RecvMsg recvMsg) throws Exception {
        switch (recvMsg.msgType) {
            case Header.MsgType.PING:
                ExecutorFactory.submitSendTask(new MessageSendTask(channelHandlerContext.channel(), recvMsg));
                break;
            case Header.MsgType.PAYLOAD:
                ExecutorFactory.submitRecvTask(new MessageRecvTask(channelHandlerContext, recvMsg));
                break;
        }
    }
}
