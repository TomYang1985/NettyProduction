package com.tencent.tvmanager.netty.core.threadpool;

import com.tencent.tvmanager.netty.msg.EMCallbackTaskMessage;
import com.tencent.tvmanager.netty.msg.Header;
import com.tencent.tvmanager.netty.msg.RecvMsg;
import com.tencent.tvmanager.util.HostUtils;

import io.netty.channel.ChannelHandlerContext;

/**
 * Created by xiaoguochang on 2017/8/27.
 */

public class MessageRecvTask implements Runnable {
    private ChannelHandlerContext mChannelHandlerContext;
    private RecvMsg mRecvMsg;

    public MessageRecvTask(ChannelHandlerContext channelHandlerContext, RecvMsg recvMsg) {
        mChannelHandlerContext = channelHandlerContext;
        mRecvMsg = recvMsg;
    }

    @Override
    public void run() {
        if (mChannelHandlerContext != null) {
            switch (mRecvMsg.msgType) {
                case Header.MsgType.PING:
                    ExecutorFactory.submitSendTask(new MessageSendTask(mChannelHandlerContext.channel(), mRecvMsg));
                    break;
                case Header.MsgType.PAYLOAD:
                    String remoteAddress = mChannelHandlerContext.channel().remoteAddress().toString();
                    EMCallbackTaskMessage message = new EMCallbackTaskMessage(mRecvMsg);
                    message.id = HostUtils.parseHostPort(remoteAddress);
                    ExecutorFactory.submitCallbackTask(new CallbackTask(message));
                    break;
            }
        }
    }
}