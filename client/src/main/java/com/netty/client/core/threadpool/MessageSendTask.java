package com.netty.client.core.threadpool;

import com.netty.client.msg.Header;
import com.netty.client.msg.PingProto;
import com.netty.client.msg.RecvMsg;
import com.netty.client.msg.SendMsg;
import com.netty.client.utils.L;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by xiaoguochang on 2017/8/27.
 */

public class MessageSendTask implements Runnable {
    private Channel mChannel;
    private RecvMsg mRecvMsg;
    private SendMsg mSendMsg;

    public MessageSendTask(Channel channel, RecvMsg recvMsg) {
        mChannel = channel;
        mRecvMsg = recvMsg;
        mSendMsg = null;
    }

    public MessageSendTask(Channel channel, SendMsg sendMsg) {
        mChannel = channel;
        mSendMsg = sendMsg;
        mRecvMsg = null;
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

        if(mSendMsg != null) {
            switch (mSendMsg.msgType) {
                case Header.MsgType.PING:
                    L.print("MessageSendTask send ping to" + mChannel.remoteAddress());
                    mChannel.writeAndFlush(PingProto.Ping.newBuilder().build());
                    break;
                case Header.MsgType.PAYLOAD:
                    L.print("MessageSendTask send payload to" + mChannel.remoteAddress());
                    mChannel.writeAndFlush(mSendMsg.data);
                    break;
            }
        }

        if(mRecvMsg != null){

        }
    }
}
