package com.netty.client.core.threadpool;


import com.netty.client.common.ConnectCode;
import com.netty.client.core.EMConnectManager;
import com.netty.client.core.EMMessageManager;
import com.netty.client.listener.EMConnectionListener;
import com.netty.client.listener.EMMessageListener;
import com.netty.client.msg.CallbackTaskMessage;
import com.netty.client.msg.EMMessage;
import com.netty.client.msg.Header;
import com.netty.client.msg.PayloadProto;

/**
 * Created by xiaoguochang on 2017/8/27.
 */

public class CallbackTask implements Runnable {
    private CallbackTaskMessage message;

    public CallbackTask(CallbackTaskMessage message) {
        this.message = message;
    }

    @Override
    public void run() {
        if (message == null) {
            return;
        }

        if (message.type == CallbackTaskMessage.MSG_TYPE_ACTIVE) {
            for (EMConnectionListener listener : EMConnectManager.getInstance().getListener()) {
                if (listener != null) {
                    listener.onActive(message.from);
                }
            }
        } else if (message.type == CallbackTaskMessage.MSG_TYPE_INACTIVE) {
            for (EMConnectionListener listener : EMConnectManager.getInstance().getListener()) {
                if (listener != null) {
                    listener.onInActive(message.from);
                }
            }
        } else if (message.type == CallbackTaskMessage.MSG_TYPE_NOT_WIFI) {
            for (EMConnectionListener listener : EMConnectManager.getInstance().getListener()) {
                if (listener != null) {
                    listener.onError(ConnectCode.CODE_NOT_WIFI);
                }
            }
        } else if (message.type == CallbackTaskMessage.MSG_TYPE_CONNECTING) {
            for (EMConnectionListener listener : EMConnectManager.getInstance().getListener()) {
                if (listener != null) {
                    listener.onError(ConnectCode.CODE_CONNECTING);
                }
            }
        } else if (message.type == CallbackTaskMessage.MSG_TYPE_CONNECTED) {
            for (EMConnectionListener listener : EMConnectManager.getInstance().getListener()) {
                if (listener != null) {
                    listener.onError(ConnectCode.CODE_CONNECTED);
                }
            }
        } else if (message.type == CallbackTaskMessage.MSG_TYPE_HOST_NULL) {
            for (EMConnectionListener listener : EMConnectManager.getInstance().getListener()) {
                if (listener != null) {
                    listener.onError(ConnectCode.CODE_HOST_NULL);
                }
            }
        } else if (message.type == CallbackTaskMessage.MSG_TYPE_CONNECT_FAIL) {
            for (EMConnectionListener listener : EMConnectManager.getInstance().getListener()) {
                if (listener != null) {
                    listener.onError(ConnectCode.CODE_CONNECT_FAIL);
                }
            }
        } else if (message.type == CallbackTaskMessage.MSG_TYPE_CONNECT_SUCC_BY_USER) {
            for (EMConnectionListener listener : EMConnectManager.getInstance().getListener()) {
                if (listener != null) {
                    listener.onConnectSuccByUser(message.from);
                }
            }
        } else {
            switch (message.recvMessage.msgType) {
                case Header.MsgType.PAYLOAD: {
                    PayloadProto.Payload chatMsg = (PayloadProto.Payload) message.recvMessage.data;
                    for (EMMessageListener listener : EMMessageManager.getInstance().getListener()) {
                        if (listener != null) {
                            EMMessage emMessage = new EMMessage(message.from, chatMsg.getContent());
                            listener.onMessageReceived(emMessage);
                        }
                    }
                }
                break;
                case Header.MsgType.EXCHANGE_KEY_RESP: {

                }
                break;
            }
        }
    }

}
