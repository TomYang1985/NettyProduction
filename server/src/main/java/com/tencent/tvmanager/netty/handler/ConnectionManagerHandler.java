package com.tencent.tvmanager.netty.handler;

import com.tencent.tvmanager.netty.core.EMMessageManager;
import com.tencent.tvmanager.netty.core.threadpool.CallbackTask;
import com.tencent.tvmanager.netty.core.threadpool.ExecutorFactory;
import com.tencent.tvmanager.netty.msg.EMCallbackTaskMessage;
import com.tencent.tvmanager.util.HostUtils;
import com.tencent.tvmanager.util.L;

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
        L.print("ConnectionManagerHandler_channelActive_" + ctx.channel().localAddress()
                + "_" + ctx.channel().remoteAddress());
        EMMessageManager.getInstance().addChannel(ctx.channel());

        EMCallbackTaskMessage message = new EMCallbackTaskMessage(EMCallbackTaskMessage.MSG_TYPE_ACTIVE);
        message.id = HostUtils.parseHostPort(ctx.channel().remoteAddress().toString());
        ExecutorFactory.submitCallbackTask(new CallbackTask(message));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        L.print("ConnectionManagerHandler_channelInactive_" + ctx.channel().localAddress()
                + "_" + ctx.channel().remoteAddress());
        EMMessageManager.getInstance().removeChannel(ctx.channel());

        EMCallbackTaskMessage message = new EMCallbackTaskMessage(EMCallbackTaskMessage.MSG_TYPE_INACTIVE);
        message.id = HostUtils.parseHostPort(ctx.channel().remoteAddress().toString());
        ExecutorFactory.submitCallbackTask(new CallbackTask(message));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        L.d(cause.toString());
    }
}
