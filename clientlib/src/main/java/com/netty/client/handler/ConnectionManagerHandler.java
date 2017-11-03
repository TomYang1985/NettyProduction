package com.netty.client.handler;

import com.netty.client.common.ETvModelID;
import com.netty.client.common.InnerMessageHelper;
import com.netty.client.core.EMMessageManager;
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
        L.writeFile("ConnectionManagerHandler.channelActive");

        EMMessageManager.getInstance().setChannel(ctx.channel());//设置消息管理的channel
        //发送动态密钥
        InnerMessageHelper.sendKey(ctx.channel());

        String remoteAddress = ctx.channel().remoteAddress().toString();
        InnerMessageHelper.sendActiveCallbackMessage(remoteAddress);

        ETvModelID.saveActionData(ETvModelID.EMID_Secure_RemoteControl_TVcommunication_ConnectTV_Successed_Count);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        L.writeFile("ConnectionManagerHandler.channelInactive");

        String remoteAddress = ctx.channel().remoteAddress().toString();
        InnerMessageHelper.sendInActiveCallbackMessage(remoteAddress);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        L.writeFile(cause.toString());
    }
}
