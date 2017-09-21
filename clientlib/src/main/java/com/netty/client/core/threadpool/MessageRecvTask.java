package com.netty.client.core.threadpool;


import com.netty.client.innermsg.NettyMessage;
import com.netty.client.innermsg.CallbackMessage;
import com.netty.client.innermsg.Header;
import com.netty.client.utils.HostUtils;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by xiaoguochang on 2017/8/27.
 */

public class MessageRecvTask implements Runnable {
    private Channel mChannel;
    private NettyMessage mMessage;

    public MessageRecvTask(Channel channel, NettyMessage message) {
        mChannel = channel;
        mMessage = message;
    }

    @Override
    public void run() {
            switch (mMessage.msgType) {
                case Header.MsgType.PAYLOAD:
                    String remoteAddress = mChannel.remoteAddress().toString();
                    CallbackMessage message = new CallbackMessage();
                    message.type = CallbackMessage.MSG_TYPE_RECV_MSG;
                    message.from = HostUtils.parseHost(remoteAddress);
                    message.recvMessage = mMessage;
                    ExecutorFactory.submitCallbackTask(new CallbackTask(message));
                    break;
            }
    }
}
