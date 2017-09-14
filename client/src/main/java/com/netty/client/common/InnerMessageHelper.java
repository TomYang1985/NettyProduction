package com.netty.client.common;

import com.google.protobuf.ByteString;
import com.netty.client.codec.Algorithm;
import com.netty.client.core.threadpool.CallbackTask;
import com.netty.client.core.threadpool.ExecutorFactory;
import com.netty.client.core.threadpool.MessageSendTask;
import com.netty.client.msg.CallbackTaskMessage;
import com.netty.client.msg.Header;
import com.netty.client.msg.KeyRequestProto;
import com.netty.client.msg.SendMessage;
import com.netty.client.utils.HostUtils;
import com.netty.client.utils.MID;

import io.netty.channel.Channel;

/**
 * Created by robincxiao on 2017/9/7.
 */

public class InnerMessageHelper {
    /**
     * 发送设备连接成功回调消息
     *
     * @param remoteAddress(host:port)
     */
    public static void sendActiveCallbackMessage(String remoteAddress) {
        CallbackTaskMessage message = new CallbackTaskMessage();
        message.type = CallbackTaskMessage.MSG_TYPE_ACTIVE;
        message.from = HostUtils.parseHost(remoteAddress);
        ExecutorFactory.submitCallbackTask(new CallbackTask(message));
    }

    /**
     * 发送设备连接成功回调消息
     *
     * @param remoteAddress(host:port)
     */
    public static void sendInActiveCallbackMessage(String remoteAddress) {
        CallbackTaskMessage message = new CallbackTaskMessage();
        message.type = CallbackTaskMessage.MSG_TYPE_INACTIVE;
        message.from = HostUtils.parseHost(remoteAddress);
        ExecutorFactory.submitCallbackTask(new CallbackTask(message));
    }

    /**
     * 发送用户直连成功回调消息
     *
     * @param host
     */
    public static void sendConnectSuccByUserMessage(String host) {
        CallbackTaskMessage message = new CallbackTaskMessage();
        message.type = CallbackTaskMessage.MSG_TYPE_CONNECT_SUCC_BY_USER;
        message.from = host;
        ExecutorFactory.submitCallbackTask(new CallbackTask(message));
    }

    /**
     * 发送错误回调消息
     *
     * @param type
     */
    public static void sendErrorCallbackMessage(int type) {
        CallbackTaskMessage message = new CallbackTaskMessage();
        message.type = type;
        ExecutorFactory.submitCallbackTask(new CallbackTask(message));
    }



    /**
     * 发送数据加密key到服务端
     */
    public static void sendKey(Channel channel) {
        byte[] keys = Algorithm.getInstance().generateAESKey();

        KeyRequestProto.KeyRequest request = KeyRequestProto.KeyRequest.newBuilder()
                .setMessageId(MID.getId())
                .setKeys(ByteString.copyFrom(keys)).build();
        SendMessage sendMessage = new SendMessage(Header.MsgType.EXCHANGE_KEY, request);
        ExecutorFactory.submitSendTask(new MessageSendTask(channel, sendMessage));
    }
}
