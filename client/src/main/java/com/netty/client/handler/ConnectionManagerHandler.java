package com.netty.client.handler;

import android.text.TextUtils;

import com.netty.client.core.EMMessageManager;
import com.netty.client.core.threadpool.CallbackTask;
import com.netty.client.core.threadpool.ExecutorFactory;
import com.netty.client.msg.EMCallbackTaskMessage;
import com.netty.client.utils.HostUtils;
import com.netty.client.utils.L;

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

        EMMessageManager.getInstance().setChannel(ctx.channel());//设置消息管理的channel

        String remoteAddress = "";
        if (ctx.channel() != null && ctx.channel().remoteAddress() != null && ctx.channel().remoteAddress().toString() != null) {
            remoteAddress = ctx.channel().remoteAddress().toString();
        }
        EMCallbackTaskMessage message = new EMCallbackTaskMessage();
        message.type = EMCallbackTaskMessage.MSG_TYPE_ACTIVE;
        message.from = HostUtils.parseHost(remoteAddress);
        ExecutorFactory.submitCallbackTask(new CallbackTask(message));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        /**
         * channelInactive方法将会在如下情况下被调用多次
         * 1.已连接的状态下断开，会调用
         * 2.关闭网络时，进行连接尝试时会被调用，但此时remoteAddress为null
         * 因此应该在connect前，判断网络环境
         */
        String remoteAddress = "";
        if (ctx.channel() != null && ctx.channel().remoteAddress() != null && ctx.channel().remoteAddress().toString() != null) {
            remoteAddress = ctx.channel().remoteAddress().toString();
        }

        if (!TextUtils.isEmpty(remoteAddress)) {
            EMCallbackTaskMessage message = new EMCallbackTaskMessage();
            message.type = EMCallbackTaskMessage.MSG_TYPE_INACTIVE;
            message.from = HostUtils.parseHost(remoteAddress);
            ExecutorFactory.submitCallbackTask(new CallbackTask(message));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        L.d("exceptionCaught");
        L.d(cause.toString());
    }
}
