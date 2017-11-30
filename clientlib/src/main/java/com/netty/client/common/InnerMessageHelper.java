package com.netty.client.common;

import com.google.protobuf.ByteString;
import com.netty.client.codec.KeyManager;
import com.netty.client.core.threadpool.CallbackTask;
import com.netty.client.core.threadpool.ExecutorFactory;
import com.netty.client.core.threadpool.MessageSendTask;
import com.netty.client.innermsg.NettyMessage;
import com.netty.client.innermsg.CallbackMessage;
import com.netty.client.innermsg.Header;
import com.netty.client.innermsg.KeyRequestProto;
import com.netty.client.utils.MID;

import io.netty.channel.Channel;

/**
 * Created by robincxiao on 2017/9/7.
 */

public class InnerMessageHelper {
    /**
     * 发送设备重连回调消息
     *
     */
    public static void sendReconnectingCallbackMessage() {
        CallbackMessage message = new CallbackMessage();
        message.type = CallbackMessage.MSG_TYPE_RECONNECT;
        ExecutorFactory.submitCallbackTask(new CallbackTask(message));
    }

    /**
     * 发送设备断开连接回调消息
     *
     */
    public static void sendDisconnectCallbackMessage() {
        CallbackMessage message = new CallbackMessage();
        message.type = CallbackMessage.MSG_TYPE_DISCONNECT;
        ExecutorFactory.submitCallbackTask(new CallbackTask(message));
    }

    /**
     * 发送错误回调消息
     *
     * @param type
     */
    public static void sendErrorCallbackMessage(int type) {
        CallbackMessage message = new CallbackMessage();
        message.type = type;
        ExecutorFactory.submitCallbackTask(new CallbackTask(message));
    }

    /**
     * 发送ping
     * @param channel
     */
    public static void sendPing(Channel channel){
        NettyMessage message = new NettyMessage();
        message.msgType = Header.MsgType.PING;
        ExecutorFactory.submitSendTask(new MessageSendTask(channel, message));
    }

    /**
     * 发送数据加密key到服务端
     */
    public static void sendKey(Channel channel) {
        byte[] keys = KeyManager.getInstance().generateAESKey();

        KeyRequestProto.KeyRequest request = KeyRequestProto.KeyRequest.newBuilder()
                .setMessageId(MID.getId())
                .setKeys(ByteString.copyFrom(keys)).build();

        NettyMessage message = new NettyMessage();
        message.msgType = Header.MsgType.EXCHANGE_KEY;
        message.body = request;
        ExecutorFactory.submitSendTask(new MessageSendTask(channel, message));
    }
}
