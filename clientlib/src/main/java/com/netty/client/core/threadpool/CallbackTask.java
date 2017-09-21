package com.netty.client.core.threadpool;


import com.netty.client.common.Code;
import com.netty.client.core.EMConnectManager;
import com.netty.client.core.EMMessageManager;
import com.netty.client.innermsg.CallbackMessage;
import com.netty.client.innermsg.Header;
import com.netty.client.listener.EMConnectionListener;
import com.netty.client.listener.EMMessageListener;
import com.netty.client.innermsg.AppActionProto;
import com.netty.client.innermsg.AppListResponseProto;
import com.netty.client.msg.EMAppInstall;
import com.netty.client.msg.EMAppRemove;
import com.netty.client.msg.EMInstalledApp;
import com.netty.client.msg.EMMessage;
import com.netty.client.msg.EMPayload;
import com.netty.client.msg.EMServerVersion;
import com.netty.client.innermsg.KeyResponseProto;
import com.netty.client.innermsg.PayloadProto;

/**
 * Created by xiaoguochang on 2017/8/27.
 */

public class CallbackTask implements Runnable {
    private CallbackMessage message;

    public CallbackTask(CallbackMessage message) {
        this.message = message;
    }


    @Override
    public void run() {
        if (message == null) {
            return;
        }

        if (message.type == CallbackMessage.MSG_TYPE_ACTIVE) {
            for (EMConnectionListener listener : EMConnectManager.getInstance().getListener()) {
                if (listener != null) {
                    listener.onActive(message.from);
                }
            }
        } else if (message.type == CallbackMessage.MSG_TYPE_INACTIVE) {
            for (EMConnectionListener listener : EMConnectManager.getInstance().getListener()) {
                if (listener != null) {
                    listener.onInActive(message.from);
                }
            }
        } else if (message.type == CallbackMessage.MSG_TYPE_NOT_WIFI) {
            connectError(Code.CODE_NOT_WIFI);
        } else if (message.type == CallbackMessage.MSG_TYPE_CONNECTING) {
            connectError(Code.CODE_CONNECTING);
        } else if (message.type == CallbackMessage.MSG_TYPE_CONNECTED) {
            connectError(Code.CODE_CONNECTED);
        } else if (message.type == CallbackMessage.MSG_TYPE_HOST_NULL) {
            connectError(Code.CODE_HOST_NULL);
        } else if (message.type == CallbackMessage.MSG_TYPE_CONNECT_FAIL) {
            connectError(Code.CODE_CONNECT_FAIL);
        } else if (message.type == CallbackMessage.MSG_TYPE_CONNECT_SUCC_BY_USER) {
            for (EMConnectionListener listener : EMConnectManager.getInstance().getListener()) {
                if (listener != null) {
                    listener.onConnectSuccByUser(message.from);
                }
            }
        } else if (message.type == CallbackMessage.MSG_TYPE_RECV_MSG) {
            switch (message.recvMessage.msgType) {
                case Header.MsgType.PAYLOAD: {
                    PayloadProto.Payload chatMsg = (PayloadProto.Payload) message.recvMessage.body;
                    EMPayload payload = new EMPayload(message.from, chatMsg.getContent());
                    callbackMessage(payload);
                }
                break;
                case Header.MsgType.EXCHANGE_KEY_RESP: {
                    KeyResponseProto.KeyResponse body = (KeyResponseProto.KeyResponse) message.recvMessage.body;
                    int versionCode = body.getVersionCode();
                    String versionName = body.getVersionName();
                    callbackMessage(new EMServerVersion(versionCode, versionName));
                }
                break;
                case Header.MsgType.RESPONSE:
                    doResponse();
                    break;
            }
        }
    }

    /**
     * 业务消息的分类处理
     */
    private void doResponse() {
        switch (message.recvMessage.businessType) {
            case Header.BusinessType.RESPONSE_APP_ADDED: {
                AppActionProto.AppAction body = (AppActionProto.AppAction) message.recvMessage.body;
                callbackMessage(new EMAppInstall(body.getPackageName(), body.getAppName(), body.getVersionCode()
                        , body.getVersionName(), body.getIsSystem()));

            }
            break;
            case Header.BusinessType.RESPONSE_APP_REMOVED: {
                AppActionProto.AppAction body = (AppActionProto.AppAction) message.recvMessage.body;
                callbackMessage(new EMAppRemove(body.getPackageName()));
            }
            break;
            case Header.BusinessType.RESPONSE_APP_LIST: {
                AppListResponseProto.AppListResponse body = (AppListResponseProto.AppListResponse) message.recvMessage.body;
                EMInstalledApp installedApps = new EMInstalledApp();
                for (AppListResponseProto.AppInfo appInfo : body.getListList()) {
                    installedApps.add(appInfo.getPackageName(), appInfo.getAppName(), appInfo.getVersionCode()
                            , appInfo.getVersionName(), appInfo.getIsSystem());
                }
                callbackMessage(installedApps);
            }
            break;
        }
    }

    /**
     * 连接错误回调
     *
     * @param code
     */
    private void connectError(int code) {
        for (EMConnectionListener listener : EMConnectManager.getInstance().getListener()) {
            if (listener != null) {
                listener.onError(code);
            }
        }
    }

    private void callbackMessage(EMMessage message) {
        for (EMMessageListener listener : EMMessageManager.getInstance().getListener()) {
            if (listener != null) {
                listener.onMessageReceived(message);
            }
        }
    }
}
