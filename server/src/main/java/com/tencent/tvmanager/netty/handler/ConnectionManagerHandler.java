package com.tencent.tvmanager.netty.handler;

import com.tencent.tvmanager.netty.codec.KeysManager;
import com.tencent.tvmanager.netty.core.EMConnectManager;
import com.tencent.tvmanager.netty.core.EMMessageManager;
import com.tencent.tvmanager.netty.core.threadpool.CallbackTask;
import com.tencent.tvmanager.netty.core.threadpool.ExecutorFactory;
import com.tencent.tvmanager.netty.innermsg.CallbackMessage;
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
        L.print("ConnectionManagerHandler_channelActive_" + ctx.channel().remoteAddress());
        //添加连接的客户端
        EMConnectManager.getInstance().addChannel(ctx.channel());
        //发送客户端连接回调
        CallbackMessage message = new CallbackMessage(CallbackMessage.MSG_TYPE_ACTIVE);
        message.id = HostUtils.parseHostPort(ctx.channel().remoteAddress().toString());
        ExecutorFactory.submitCallbackTask(new CallbackTask(message));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        L.print("ConnectionManagerHandler_channelInactive_" + ctx.channel().remoteAddress());
        //移除断开的客户端
        EMConnectManager.getInstance().removeChannel(ctx.channel());
        //发送客户端断开回调
        CallbackMessage message = new CallbackMessage(CallbackMessage.MSG_TYPE_INACTIVE);
        String clientId = HostUtils.parseHostPort(ctx.channel().remoteAddress().toString());
        message.id = clientId;
        ExecutorFactory.submitCallbackTask(new CallbackTask(message));
        //client关闭时，从KeysManager中移除
        KeysManager.getInstance().removeKey(clientId);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        L.d(cause.toString());
    }
}
