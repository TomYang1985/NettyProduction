package com.tencent.tvmanager.netty.common;

import android.content.Context;

import com.tencent.tvmanager.netty.core.EMAcceptor;
import com.tencent.tvmanager.netty.innermsg.NettyMessage;
import com.tencent.tvmanager.netty.innermsg.Header;
import com.tencent.tvmanager.netty.msg.KeyResponseProto;
import com.tencent.tvmanager.util.AppUtils;
import com.tencent.tvmanager.util.MID;

import io.netty.channel.Channel;

/**
 * Created by robincxiao on 2017/9/7.
 */

public class InnerMessageHelper {
    /**
     * 发送pong
     * @param channel
     */
    public static void sendPong(Channel channel){
        NettyMessage message = new NettyMessage();
        message.msgType = Header.MsgType.PONG;

        channel.writeAndFlush(message);
    }

    /**
     * 发送密钥交换响应
     * @param channel
     */
    public static void sendKeyResponseSucc(Channel channel) {
        Context context = EMAcceptor.getInstance().getContext();
        String versionName = "";
        int versionCode = -1;
        if (context != null) {
            versionName = AppUtils.getVersionName(context);
            versionCode = AppUtils.getVersionCode(context);
        }

        KeyResponseProto.KeyResponse response = KeyResponseProto.KeyResponse.newBuilder()
                .setMessageId(MID.getId())
                .setCode(Code.RESULT_OK)
                .setVersionCode(versionCode)
                .setVersionName(versionName)
                .build();

        NettyMessage message = new NettyMessage();
        message.msgType = Header.MsgType.EXCHANGE_KEY_RESP;
        message.body = response;

        channel.writeAndFlush(message);
    }

}
