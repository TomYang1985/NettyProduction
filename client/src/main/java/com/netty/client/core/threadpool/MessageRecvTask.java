package com.netty.client.core.threadpool;


import com.netty.client.msg.CallbackTaskMessage;
import com.netty.client.msg.Header;
import com.netty.client.msg.RecvMessage;
import com.netty.client.utils.HostUtils;

import io.netty.channel.ChannelHandlerContext;

/**
 * Created by xiaoguochang on 2017/8/27.
 */

public class MessageRecvTask implements Runnable {
    private ChannelHandlerContext mChannelHandlerContext;
    private RecvMessage mRecvMessage;

    public MessageRecvTask(ChannelHandlerContext channelHandlerContext, RecvMessage recvMessage) {
        mChannelHandlerContext = channelHandlerContext;
        mRecvMessage = recvMessage;
    }

    @Override
    public void run() {
        if (mRecvMessage != null) {
            switch (mRecvMessage.msgType) {
                case Header.MsgType.PAYLOAD:
                    String remoteAddress = mChannelHandlerContext.channel().remoteAddress().toString();
                    CallbackTaskMessage message = new CallbackTaskMessage();
                    message.recvMessage = mRecvMessage;
                    message.from = HostUtils.parseHost(remoteAddress);
                    ExecutorFactory.submitCallbackTask(new CallbackTask(message));
                    break;
            }
        }
    }
}
