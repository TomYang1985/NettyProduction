package com.tencent.tvmanager.netty.common;

import android.content.Context;

import com.tencent.tvmanager.netty.core.EMAcceptor;
import com.tencent.tvmanager.netty.msg.KeyResponseProto;
import com.tencent.tvmanager.util.AppUtils;
import com.tencent.tvmanager.util.MID;

import io.netty.channel.Channel;

/**
 * Created by robincxiao on 2017/9/7.
 */

public class InnerMessageHelper {
    public static void sendKeyResponseSucc(Channel mChannel) {
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

        mChannel.writeAndFlush(response);
    }

}
