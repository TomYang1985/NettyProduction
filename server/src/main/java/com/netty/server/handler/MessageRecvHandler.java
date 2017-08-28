package com.netty.server.handler;

import com.netty.server.core.threadpool.MessageRecvExecutor;
import com.netty.server.core.threadpool.MessageRecvTask;
import com.netty.server.core.threadpool.MessageSendExecutor;
import com.netty.server.core.threadpool.MessageSendTask;
import com.netty.server.msg.Header;
import com.netty.server.msg.PongProto;
import com.netty.server.msg.ReceiveMsg;
import com.netty.server.msg.SendMsg;
import com.netty.server.utils.L;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by robincxiao on 2017/8/23.
 */
@ChannelHandler.Sharable
public class MessageRecvHandler extends SimpleChannelInboundHandler<ReceiveMsg> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ReceiveMsg receiveMsg) throws Exception {
        switch (receiveMsg.msgType){
            case Header.PING:
            case Header.CHAT_MSG:
                MessageSendExecutor.submit(new MessageSendTask(channelHandlerContext, receiveMsg));
                break;
        }
    }
}
