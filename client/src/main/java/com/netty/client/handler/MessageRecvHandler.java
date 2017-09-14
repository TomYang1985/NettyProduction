package com.netty.client.handler;

import com.netty.client.core.threadpool.CallbackTask;
import com.netty.client.core.threadpool.ExecutorFactory;
import com.netty.client.core.threadpool.MessageRecvTask;
import com.netty.client.msg.CallbackTaskMessage;
import com.netty.client.msg.Header;
import com.netty.client.msg.RecvMessage;
import com.netty.client.utils.HostUtils;
import com.netty.client.utils.L;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by robincxiao on 2017/8/23.
 */
@ChannelHandler.Sharable
public class MessageRecvHandler extends SimpleChannelInboundHandler<RecvMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RecvMessage recvMessage) throws Exception {
        switch (recvMessage.msgType){
            case Header.MsgType.PAYLOAD:
                ExecutorFactory.submitRecvTask(new MessageRecvTask(channelHandlerContext, recvMessage));
                break;
            case Header.MsgType.EXCHANGE_KEY_RESP:
                CallbackTaskMessage message = new CallbackTaskMessage();
                message.from = channelHandlerContext.channel().remoteAddress().toString();
                message.recvMessage = recvMessage;
                ExecutorFactory.submitCallbackTask(new CallbackTask(message));
                //ExecutorFactory.submitRecvTask(new MessageRecvTask(channelHandlerContext, recvMessage));
                break;
        }
    }
}
