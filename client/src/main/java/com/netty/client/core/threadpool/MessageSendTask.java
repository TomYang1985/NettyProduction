package com.netty.client.core.threadpool;

import com.netty.client.msg.Header;
import com.netty.client.msg.PingProto;
import com.netty.client.msg.ReceiveMsg;
import com.netty.client.msg.SendMsg;
import com.netty.client.utils.L;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by xiaoguochang on 2017/8/27.
 */

public class MessageSendTask implements Runnable {
    private ChannelHandlerContext mChannelHandlerContext;
    private ReceiveMsg mReceiveMsg;
    private SendMsg mSendMsg;

    public MessageSendTask(ChannelHandlerContext channelHandlerContext, ReceiveMsg receiveMsg) {
        mChannelHandlerContext = channelHandlerContext;
        mReceiveMsg = receiveMsg;
    }

    public MessageSendTask(ChannelHandlerContext channelHandlerContext, SendMsg sendMsg) {
        mChannelHandlerContext = channelHandlerContext;
        mSendMsg = sendMsg;
    }

    @Override
    public void run() {
        if (mChannelHandlerContext == null) {
            L.print("MessageSendTask mChannelHandlerContext = null");
            return;
        }
        Channel channel = mChannelHandlerContext.channel();
        if (!channel.isActive()) {
            L.print("MessageSendTask channel != Active");
            return;
        }
        if (!channel.isWritable()) {
            L.print("MessageSendTask channel is not Writable");
            return;
        }

        if(mSendMsg != null) {
            switch (mSendMsg.msgType) {
                case Header.PING:
                    L.print("MessageSendTask send ping to" + channel.remoteAddress());
                    channel.writeAndFlush(PingProto.Ping.newBuilder().build());
                    break;
                case Header.CHAT_MSG:
                    L.print("MessageSendTask send chatMsg to" + channel.remoteAddress());
                    channel.writeAndFlush(mSendMsg.data);
                    break;
            }
        }

        if(mReceiveMsg != null){

        }
    }
}
