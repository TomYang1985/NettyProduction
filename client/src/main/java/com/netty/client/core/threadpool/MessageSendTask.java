package com.netty.client.core.threadpool;

import com.netty.client.innermsg.NettyMessage;
import com.netty.client.innermsg.Header;
import com.netty.client.utils.L;

import io.netty.channel.Channel;

/**
 * Created by xiaoguochang on 2017/8/27.
 */

public class MessageSendTask implements Runnable {
    private Channel mChannel;
    private NettyMessage mMessage;

    public MessageSendTask(Channel channel, NettyMessage message) {
        mChannel = channel;
        mMessage = message;
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

        switch (mMessage.msgType) {
            case Header.MsgType.PING:
                L.print("MessageSendTask send ping to" + mChannel.remoteAddress());
                mChannel.writeAndFlush(mMessage);
                break;
            case Header.MsgType.PAYLOAD:
                mChannel.writeAndFlush(mMessage);
                break;
            case Header.MsgType.EXCHANGE_KEY://发送key
                mChannel.writeAndFlush(mMessage);
                break;
        }

    }
}
