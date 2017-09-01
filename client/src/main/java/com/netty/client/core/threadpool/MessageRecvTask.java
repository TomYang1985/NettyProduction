package com.netty.client.core.threadpool;


import com.netty.client.msg.EMCallbackTaskMessage;
import com.netty.client.msg.Header;
import com.netty.client.msg.RecvMsg;

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
                case Header.PAYLOAD:
                    String remoteAddress = mChannelHandlerContext.channel().remoteAddress().toString();
                    ExecutorFactory.submitCallbackTask(new CallbackTask(new EMCallbackTaskMessage(mRecvMsg), remoteAddress));
                    //ChatProto.Chat recvChat = (ChatProto.Chat) mRecvMsg.data;
                    //L.print("recv " + recvChat.getContent() + "msg from" + recvChat.getAddress());
                    break;
            }
        }
    }
}
