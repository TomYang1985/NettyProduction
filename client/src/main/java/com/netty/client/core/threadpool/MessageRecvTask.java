package com.netty.client.core.threadpool;


import com.netty.client.msg.EMCallbackTaskMessage;
import com.netty.client.msg.Header;
import com.netty.client.msg.RecvMsg;
import com.netty.client.utils.HostUtils;

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
        if (mRecvMsg != null) {
            switch (mRecvMsg.msgType) {
                case Header.MsgType.PAYLOAD:
                    String remoteAddress = mChannelHandlerContext.channel().remoteAddress().toString();
                    EMCallbackTaskMessage message = new EMCallbackTaskMessage();
                    message.mRecvMsg = mRecvMsg;
                    message.from = HostUtils.parseHost(remoteAddress);
                    ExecutorFactory.submitCallbackTask(new CallbackTask(message));
                    break;
            }
        }
    }
}
