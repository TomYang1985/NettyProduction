package com.tencent.tvmanager.netty.handler;

import com.tencent.tvmanager.netty.core.threadpool.ExecutorFactory;
import com.tencent.tvmanager.netty.core.threadpool.MessageRecvTask;
import com.tencent.tvmanager.netty.core.threadpool.MessageSendTask;
import com.tencent.tvmanager.netty.innermsg.NettyMessage;
import com.tencent.tvmanager.netty.innermsg.Header;
import com.tencent.tvmanager.util.L;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by robincxiao on 2017/8/23.
 */
@ChannelHandler.Sharable
public class MessageRecvHandler extends SimpleChannelInboundHandler<NettyMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, NettyMessage message) throws Exception {
        switch (message.msgType) {
            case Header.MsgType.PING:
                ExecutorFactory.submitSendTask(new MessageSendTask(channelHandlerContext.channel(), message));
                break;
            case Header.MsgType.PAYLOAD:
                ExecutorFactory.submitRecvTask(new MessageRecvTask(channelHandlerContext.channel(), message));
                break;
            case Header.MsgType.EXCHANGE_KEY:
                ExecutorFactory.submitSendTask(new MessageSendTask(channelHandlerContext.channel(), message));
                break;
            case Header.MsgType.REQUEST:
                doBusiness(channelHandlerContext, message);
                break;
        }
    }

    /**
     * 业务请求分发处理
     * @param channelHandlerContext
     * @param message
     */
    private void doBusiness(ChannelHandlerContext channelHandlerContext, NettyMessage message){
        switch (message.businessType){
            case Header.BusinessType.REQUEST_APP_LIST:
            case Header.BusinessType.REQUEST_TV_UPDATE:
            case Header.BusinessType.REQUEST_CLEAN:
                ExecutorFactory.submitRecvTask(new MessageRecvTask(channelHandlerContext.channel(), message));
                break;
        }
    }
}

