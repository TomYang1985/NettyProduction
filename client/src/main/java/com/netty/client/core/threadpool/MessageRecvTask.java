package com.netty.client.core.threadpool;



import com.netty.client.msg.ChatProto;
import com.netty.client.msg.Header;
import com.netty.client.msg.ReceiveMsg;
import com.netty.client.msg.SendMsg;
import com.netty.client.utils.L;

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
        if(mReceiveMsg != null){
            switch (mReceiveMsg.msgType){
                case Header.CHAT_MSG:
                    ChatProto.Chat recvChat = (ChatProto.Chat) mReceiveMsg.data;
                    L.print("recv " + recvChat.getContent() +  "msg from" + recvChat.getAddress());
                    break;
            }
        }
    }
}
