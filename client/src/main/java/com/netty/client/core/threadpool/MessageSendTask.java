package com.netty.client.core.threadpool;

import com.netty.client.msg.Header;
import com.netty.client.msg.PingProto;
import com.netty.client.msg.RecvMessage;
import com.netty.client.msg.SendMessage;
import com.netty.client.utils.L;

import io.netty.channel.Channel;

/**
 * Created by xiaoguochang on 2017/8/27.
 */

public class MessageSendTask implements Runnable {
    private Channel mChannel;
    private RecvMessage mRecvMessage;
    private SendMessage mSendMessage;

    public MessageSendTask(Channel channel, RecvMessage recvMessage) {
        mChannel = channel;
        mRecvMessage = recvMessage;
        mSendMessage = null;
    }

    public MessageSendTask(Channel channel, SendMessage sendMessage) {
        mChannel = channel;
        mSendMessage = sendMessage;
        mRecvMessage = null;
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

        if(mSendMessage != null) {
            switch (mSendMessage.msgType) {
                case Header.MsgType.PING:
                    L.print("MessageSendTask send ping to" + mChannel.remoteAddress());
                    mChannel.writeAndFlush(PingProto.Ping.newBuilder().build());
                    break;
                case Header.MsgType.PAYLOAD:
                    mChannel.writeAndFlush(mSendMessage.data);
                    break;
                case Header.MsgType.EXCHANGE_KEY://发送key
                    mChannel.writeAndFlush(mSendMessage.data);
                    break;
            }
        }

        if(mRecvMessage != null){

        }
    }
}
