package com.netty.server.core.threadpool;

import com.netty.client.msg.ChatProto;
import com.netty.server.msg.Header;
import com.netty.server.msg.PongProto;
import com.netty.server.msg.ReceiveMsg;
import com.netty.server.msg.SendMsg;
import com.netty.server.utils.L;

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

        if (mSendMsg != null) {

        }

        if (mReceiveMsg != null) {
            switch (mReceiveMsg.msgType) {
                case Header.PING:
                    L.print("MessageSendTask send pong to" + channel.remoteAddress());
                    channel.writeAndFlush(PongProto.Pong.newBuilder().build());//收到ping，发送pong
                    break;
                case Header.CHAT_MSG:
                    ChatProto.Chat recvChat = (ChatProto.Chat) mReceiveMsg.data;
                    L.print("recv " + recvChat.getContent() + "msg from" + recvChat.getAddress());
                    L.print("MessageSendTask send chatmsg to" + channel.remoteAddress());
                    ChatProto.Chat chat = ChatProto.Chat.newBuilder()
                            .setAddress(mChannelHandlerContext.channel().localAddress().toString())
                            .setContent("我收到你的消息啦，哈哈哈").build();
                    channel.writeAndFlush(chat);
                    break;
            }
        }
    }
}
