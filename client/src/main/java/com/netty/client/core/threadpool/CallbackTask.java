package com.netty.client.core.threadpool;


import android.text.TextUtils;

import com.netty.client.core.EMConnectManager;
import com.netty.client.core.EMMessageManager;
import com.netty.client.listener.EMConnectionListener;
import com.netty.client.listener.EMMessageListener;
import com.netty.client.msg.EMCallbackTaskMessage;
import com.netty.client.msg.EMMessage;
import com.netty.client.msg.Header;
import com.netty.client.msg.PayloadProto;
import com.netty.client.multicast.EMDevice;
import com.netty.client.utils.HostUtils;

/**
 * Created by xiaoguochang on 2017/8/27.
 */

public class CallbackTask implements Runnable {
    private EMCallbackTaskMessage message;

    public CallbackTask(EMCallbackTaskMessage message) {
        this.message = message;
    }

    @Override
    public void run() {
        if (message == null) {
            return;
        }

        if (message.type == EMCallbackTaskMessage.MSG_TYPE_ACTIVE) {
            for (EMConnectionListener listener : EMConnectManager.getInstance().getListener()) {
                if (listener != null) {
                    listener.onConnected(message.from);
                }
            }
        } else if (message.type == EMCallbackTaskMessage.MSG_TYPE_INACTIVE) {
            for (EMConnectionListener listener : EMConnectManager.getInstance().getListener()) {
                if (listener != null) {
                    listener.onDisconnected(message.from);
                }
            }
        } else {
            switch (message.mRecvMsg.msgType) {
                case Header.MsgType.PAYLOAD:
                    for (EMMessageListener listener : EMMessageManager.getInstance().getListener()) {
                        if (listener != null) {
                            PayloadProto.Payload chatMsg = (PayloadProto.Payload) message.mRecvMsg.data;
                            EMMessage emMessage = new EMMessage(message.from, chatMsg.getContent());
                            listener.onMessageReceived(emMessage);
                        }
                    }
                    break;
            }
        }
    }

}
