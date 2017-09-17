package com.netty.client.core.threadpool;


import com.netty.client.common.Code;
import com.netty.client.core.EMConnectManager;
import com.netty.client.core.EMMessageManager;
import com.netty.client.listener.EMConnectionListener;
import com.netty.client.listener.EMMessageListener;
import com.netty.client.innermsg.CallbackMessage;
import com.netty.client.msg.EMMessage;
import com.netty.client.innermsg.Header;
import com.netty.client.msg.EMPayload;
import com.netty.client.msg.EMServerVersion;
import com.netty.client.msg.KeyResponseProto;
import com.netty.client.msg.PayloadProto;

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
        } else if (message.type == CallbackMessage.MSG_TYPE_RECV_MSG){
            switch (message.recvMessage.msgType) {
                case Header.MsgType.PAYLOAD: {
                    PayloadProto.Payload chatMsg = (PayloadProto.Payload) message.recvMessage.body;
                    EMPayload payload = new EMPayload(message.from, chatMsg.getContent());
                    callbackMessage(payload);
                }
                break;
                case Header.MsgType.EXCHANGE_KEY_RESP: {
                    KeyResponseProto.KeyResponse response = (KeyResponseProto.KeyResponse) message.recvMessage.body;
                    int versionCode = response.getVersionCode();
                    String versionName = response.getVersionName();
                    callbackMessage(new EMServerVersion(versionCode, versionName));
                }
                break;
            }
        }
    }

    private void connectError(int code){
        for (EMConnectionListener listener : EMConnectManager.getInstance().getListener()) {
            if (listener != null) {
                listener.onError(code);
            }
        }
    }

    private void callbackMessage(EMMessage message){
        for (EMMessageListener listener : EMMessageManager.getInstance().getListener()) {
            if (listener != null) {
                listener.onMessageReceived(message);
            }
        }
    }
}
