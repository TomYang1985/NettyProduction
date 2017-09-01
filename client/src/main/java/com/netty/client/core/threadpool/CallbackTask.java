package com.netty.client.core.threadpool;


import android.text.TextUtils;

import com.netty.client.core.EMConnectManager;
import com.netty.client.core.EMMessageManager;
import com.netty.client.listener.EMConnectionListener;
import com.netty.client.listener.EMMessageListener;
import com.netty.client.msg.ChatProto;
import com.netty.client.msg.EMCallbackTaskMessage;
import com.netty.client.msg.EMMessage;
import com.netty.client.msg.Header;

/**
 * Created by xiaoguochang on 2017/8/27.
 */

public class CallbackTask implements Runnable {
    private EMCallbackTaskMessage message;
    private String from;

    public CallbackTask(EMCallbackTaskMessage message) {
        this.message = message;
    }

    public CallbackTask(EMCallbackTaskMessage message, String from) {
        this.message = message;
        this.from = from;
    }

    @Override
    public void run() {
        if (message == null) {
            return;
        }

        if (message.type == EMCallbackTaskMessage.MSG_TYPE_ACTIVE) {
            for (EMConnectionListener listener : EMConnectManager.getInstance().getListener()) {
                if (listener != null) {
                    listener.onConnected();
                }
            }
        } else if (message.type == EMCallbackTaskMessage.MSG_TYPE_INACTIVE) {
            for (EMConnectionListener listener : EMConnectManager.getInstance().getListener()) {
                if (listener != null) {
                    listener.onDisconnected();
                }
            }
        } else {
            switch (message.mRecvMsg.msgType) {
                case Header.PAYLOAD:
                    for (EMMessageListener listener : EMMessageManager.getInstance().getListener()) {
                        if (listener != null) {
                            ChatProto.Chat chatMsg = (ChatProto.Chat) message.mRecvMsg.data;
                            EMMessage message = new EMMessage(getHost(from), chatMsg.getContent());
                            listener.onMessageReceived(message);
                        }
                    }
                    break;
            }
        }
    }

    private String getHost(String from){
        if(!TextUtils.isEmpty(from)){
            String[] array = from.split(":");
            return array[0].substring(1, array[0].length());
        }

        return "Null";
    }
}
