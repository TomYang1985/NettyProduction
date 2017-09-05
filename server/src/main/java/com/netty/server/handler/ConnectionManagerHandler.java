package com.netty.server.handler;

import com.netty.server.core.EMConnectManager;
import com.netty.server.core.EMMessageManager;
import com.netty.server.core.threadpool.CallbackTask;
import com.netty.server.core.threadpool.ExecutorFactory;
import com.netty.server.msg.EMCallbackTaskMessage;
import com.netty.server.utils.HostUtils;
import com.netty.server.utils.L;

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
        L.print("exceptionCaught");
        L.d(cause.toString());
    }
}
