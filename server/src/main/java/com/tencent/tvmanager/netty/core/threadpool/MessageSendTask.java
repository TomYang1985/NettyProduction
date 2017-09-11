package com.tencent.tvmanager.netty.core.threadpool;

import com.tencent.tvmanager.netty.msg.Header;
import com.tencent.tvmanager.netty.msg.PongProto;
import com.tencent.tvmanager.netty.msg.RecvMsg;
import com.tencent.tvmanager.netty.msg.SendMsg;
import com.tencent.tvmanager.util.L;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by xiaoguochang on 2017/8/27.
 */

public class MessageSendTask implements Runnable {
    private ChannelHandlerContext mChannelHandlerContext;
    private Channel mChannel;
    private RecvMsg mRecvMsg;
    private SendMsg mSendMsg;

    public MessageSendTask(Channel channel, RecvMsg recvMsg) {
        mChannel = channel;
        mRecvMsg = recvMsg;
    }

    public MessageSendTask(Channel channel, SendMsg sendMsg) {
        mChannel = channel;
        mSendMsg = sendMsg;
    }

    @Override
    public void run() {
        if (mChannel == null) {
            L.print("MessageSendTask mChannel = null");
            return;
        }

        if (!mChannel.isActive()) {
            L.print("MessageSendTask channel != Active");
            return;
        }
        if (!mChannel.isWritable()) {
            L.print("MessageSendTask channel is not Writable");
            return;
        }

        if (mSendMsg != null) {
            switch (mSendMsg.msgType) {
                case Header.MsgType.PAYLOAD:
                    mChannel.writeAndFlush(mSendMsg.data);
                    break;
            }
        }

        if (mRecvMsg != null) {
            switch (mRecvMsg.msgType) {
                case Header.MsgType.PING:
                    L.print("MessageSendTask send pong to" + mChannel.remoteAddress());
                    mChannel.writeAndFlush(PongProto.Pong.newBuilder().build());//收到ping，发送pong
                    break;
                case Header.MsgType.PAYLOAD:

                    break;
            }
        }
    }
}
