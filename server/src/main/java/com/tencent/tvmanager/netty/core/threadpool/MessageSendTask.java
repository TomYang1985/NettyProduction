package com.tencent.tvmanager.netty.core.threadpool;

import com.tencent.tvmanager.netty.common.InnerMessageHelper;
import com.tencent.tvmanager.netty.innermsg.NettyMessage;
import com.tencent.tvmanager.netty.innermsg.Header;
import com.tencent.tvmanager.util.L;

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

        switch (mMessage.msgType){
            case Header.MsgType.PING:
                L.print("MessageSendTask send pong to" + mChannel.remoteAddress());
                InnerMessageHelper.sendPong(mChannel);
                break;
            case Header.MsgType.EXCHANGE_KEY:
                InnerMessageHelper.sendKeyResponseSucc(mChannel);
                break;
            case Header.MsgType.PAYLOAD:
                mChannel.writeAndFlush(mMessage);
                break;
        }
    }
}
