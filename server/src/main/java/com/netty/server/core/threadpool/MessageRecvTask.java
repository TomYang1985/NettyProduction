package com.netty.server.core.threadpool;

import com.netty.server.msg.Header;
import com.netty.server.msg.PongProto;
import com.netty.server.msg.ReceiveMsg;
import com.netty.server.msg.SendMsg;
import com.netty.server.utils.L;

import io.netty.channel.ChannelHandlerContext;

/**
 * Created by xiaoguochang on 2017/8/27.
 */

public class MessageRecvTask implements Runnable {
    private ChannelHandlerContext mChannelHandlerContext;
    private ReceiveMsg mReceiveMsg;

    public MessageRecvTask(ChannelHandlerContext channelHandlerContext, ReceiveMsg receiveMsg) {
        mChannelHandlerContext = channelHandlerContext;
        mReceiveMsg = receiveMsg;
    }

    @Override
    public void run() {
        if(mChannelHandlerContext != null) {
            switch (mReceiveMsg.msgType) {
                case Header.PING:
                    MessageSendExecutor.submit(new MessageSendTask(mChannelHandlerContext, mReceiveMsg));
                    break;
            }
        }
    }
}
