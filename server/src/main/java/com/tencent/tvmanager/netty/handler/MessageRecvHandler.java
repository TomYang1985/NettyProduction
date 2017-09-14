package com.tencent.tvmanager.netty.handler;

import com.tencent.tvmanager.netty.core.threadpool.ExecutorFactory;
import com.tencent.tvmanager.netty.core.threadpool.MessageRecvTask;
import com.tencent.tvmanager.netty.core.threadpool.MessageSendTask;
import com.tencent.tvmanager.netty.msg.Header;
import com.tencent.tvmanager.netty.msg.RecvMsg;

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
            case Header.MsgType.EXCHANGE_KEY:
                ExecutorFactory.submitSendTask(new MessageSendTask(channelHandlerContext.channel(), recvMsg));
                break;
        }
    }
}
