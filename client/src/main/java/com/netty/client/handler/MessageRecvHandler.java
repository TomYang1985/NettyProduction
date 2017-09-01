package com.netty.client.handler;

import com.netty.client.core.threadpool.ExecutorFactory;
import com.netty.client.core.threadpool.MessageRecvTask;
import com.netty.client.msg.Header;
import com.netty.client.msg.RecvMsg;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by robincxiao on 2017/8/23.
 */
@ChannelHandler.Sharable
public class MessageRecvHandler extends SimpleChannelInboundHandler<RecvMsg>{
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RecvMsg recvMsg) throws Exception {
        switch (recvMsg.msgType){
            case Header.PAYLOAD:
                ExecutorFactory.submitRecvTask(new MessageRecvTask(channelHandlerContext, recvMsg));
                break;
        }
    }
}
