package com.netty.client.handler;

import android.text.TextUtils;

import com.netty.client.common.InnerMessageHelper;
import com.netty.client.core.EMMessageManager;
import com.netty.client.core.threadpool.CallbackTask;
import com.netty.client.core.threadpool.ExecutorFactory;
import com.netty.client.msg.CallbackTaskMessage;
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
        L.print("ConnectionManagerHandler.channelActive");

        EMMessageManager.getInstance().setChannel(ctx.channel());//设置消息管理的channel

//        String remoteAddress = "";
//        if (ctx.channel() != null && ctx.channel().remoteAddress() != null && ctx.channel().remoteAddress().toString() != null) {
//            remoteAddress = ctx.channel().remoteAddress().toString();
//        }
        InnerMessageHelper.sendKey(ctx.channel());

        String remoteAddress = ctx.channel().remoteAddress().toString();
        InnerMessageHelper.sendActiveCallbackMessage(remoteAddress);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        L.print("ConnectionManagerHandler.channelInactive");

//        String remoteAddress = "";
//        if (ctx.channel() != null && ctx.channel().remoteAddress() != null && ctx.channel().remoteAddress().toString() != null) {
//            remoteAddress = ctx.channel().remoteAddress().toString();
//        }
        String remoteAddress = ctx.channel().remoteAddress().toString();
        InnerMessageHelper.sendInActiveCallbackMessage(remoteAddress);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        L.d(cause.toString());
    }
}
