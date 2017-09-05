package com.netty.server.core.threadpool;

import com.netty.server.msg.EMCallbackTaskMessage;
import com.netty.server.msg.Header;
import com.netty.server.msg.RecvMsg;
import com.netty.server.utils.HostUtils;

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
