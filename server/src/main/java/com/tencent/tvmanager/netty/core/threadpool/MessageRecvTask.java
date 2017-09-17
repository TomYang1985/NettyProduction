package com.tencent.tvmanager.netty.core.threadpool;

import com.tencent.tvmanager.netty.innermsg.NettyMessage;
import com.tencent.tvmanager.netty.innermsg.CallbackMessage;
import com.tencent.tvmanager.netty.innermsg.Header;
import com.tencent.tvmanager.util.HostUtils;

import io.netty.channel.ChannelHandlerContext;

/**
 * Created by xiaoguochang on 2017/8/27.
 */

public class MessageRecvTask implements Runnable {
    private ChannelHandlerContext mChannelHandlerContext;
    private NettyMessage mMessage;

    public MessageRecvTask(ChannelHandlerContext channelHandlerContext, NettyMessage message) {
        mChannelHandlerContext = channelHandlerContext;
        mMessage = message;
    }

    @Override
    public void run() {
        if (mChannelHandlerContext != null) {
            switch (mMessage.msgType) {
                case Header.MsgType.PAYLOAD:
                    String remoteAddress = mChannelHandlerContext.channel().remoteAddress().toString();
                    CallbackMessage message = new CallbackMessage();
                    message.type = CallbackMessage.MSG_TYPE_RECV_MSG;
                    message.id = HostUtils.parseHostPort(remoteAddress);
                    message.recvMsg = mMessage;
                    ExecutorFactory.submitCallbackTask(new CallbackTask(message));
                    break;
            }
        }
    }
}
