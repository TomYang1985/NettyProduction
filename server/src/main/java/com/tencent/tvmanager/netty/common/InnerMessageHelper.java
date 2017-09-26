package com.tencent.tvmanager.netty.common;

import android.content.Context;

import com.tencent.tvmanager.netty.business.BusinessHelper;
import com.tencent.tvmanager.netty.core.EMAcceptor;
import com.tencent.tvmanager.netty.innermsg.AppActionResponseProto;
import com.tencent.tvmanager.netty.innermsg.Header;
import com.tencent.tvmanager.netty.innermsg.KeyResponseProto;
import com.tencent.tvmanager.netty.innermsg.NettyMessage;
import com.tencent.tvmanager.netty.util.MID;
import com.tencent.tvmanager.util.AppUtils;

/**
 * Created by robincxiao on 2017/9/7.
 */

public class InnerMessageHelper {
    /**
     * 创建pong
     */
    public static NettyMessage createPong() {
        NettyMessage message = new NettyMessage();
        message.msgType = Header.MsgType.PONG;

        return message;
    }

    /**
     * 创建密钥交换响应
     */
    public static NettyMessage createKeyResponseSucc() {
        Context context = EMAcceptor.getInstance().getContext();
        String versionName = "";
        int versionCode = 0;
        if (context != null) {
            versionName = AppUtils.getVersionName(context);
            versionCode = AppUtils.getVersionCode(context);
        }

        KeyResponseProto.KeyResponse body = KeyResponseProto.KeyResponse.newBuilder()
                .setMessageId(MID.getId())
                .setCode(Code.RESULT_OK)
                .setVersionCode(versionCode)
                .setVersionName(versionName)
                .setProtocol(Header.PROTOCOL_VERSION)
                .build();

        NettyMessage message = new NettyMessage();
        message.msgType = Header.MsgType.EXCHANGE_KEY_RESP;
        message.body = body;

        return message;
    }

    /**
     * 创建应用安装、卸载的message
     *
     * @param type        类型：1：安装、2：卸载
     * @param packageName
     * @return
     */
    public static NettyMessage createAppAction(int type, String packageName) {
        NettyMessage message = new NettyMessage();
        message.msgType = Header.MsgType.RESPONSE;
        switch (type) {
            case 1:
                message.businessType = Header.BusinessType.RESPONSE_APP_ADDED;
                message.body = BusinessHelper.getAppInfo(packageName);
                break;
            case 2:
                message.businessType = Header.BusinessType.RESPONSE_APP_REMOVED;
                message.body = AppActionResponseProto.AppActionResponse.newBuilder()
                        .setMessageId(MID.getId())
                        .setPackageName(packageName)
                        .build();
                ;
                break;
            case 3:
                break;
        }

        return message;
    }

    public static NettyMessage createAppList(String localHost) {
        NettyMessage message = new NettyMessage();
        message.msgType = Header.MsgType.RESPONSE;
        message.businessType = Header.BusinessType.RESPONSE_APP_LIST;
        message.body = BusinessHelper.getPackages(localHost);

        return message;
    }
}
