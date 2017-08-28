package com.netty.client.handler;

import com.netty.client.core.threadpool.MessageRecvExecutor;
import com.netty.client.core.threadpool.MessageRecvTask;
import com.netty.client.core.threadpool.MessageSendExecutor;
import com.netty.client.core.threadpool.MessageSendTask;
import com.netty.client.msg.Header;
import com.netty.client.msg.ReceiveMsg;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by robincxiao on 2017/8/23.
 */
@ChannelHandler.Sharable
public class MessageRecvHandler extends SimpleChannelInboundHandler<ReceiveMsg>{
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ReceiveMsg receiveMsg) throws Exception {
        switch (receiveMsg.msgType){
            case Header.CHAT_MSG:
                MessageRecvExecutor.submit(new MessageRecvTask(channelHandlerContext, receiveMsg));
                break;
        }
    }
}
