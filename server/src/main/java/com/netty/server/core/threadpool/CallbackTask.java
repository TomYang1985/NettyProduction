package com.netty.server.core.threadpool;


import com.netty.server.core.EMConnectManager;
import com.netty.server.core.EMMessageManager;
import com.netty.server.listener.EMConnectionListener;
import com.netty.server.listener.EMMessageListener;
import com.netty.server.msg.EMCallbackTaskMessage;
import com.netty.server.msg.EMDevice;
import com.netty.server.msg.EMMessage;
import com.netty.server.msg.Header;
import com.netty.server.msg.PayloadProto;

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
                    listener.onConnected(new EMDevice(message.id));
                }
            }
        } else if (message.type == EMCallbackTaskMessage.MSG_TYPE_INACTIVE) {
            for (EMConnectionListener listener : EMConnectManager.getInstance().getListener()) {
                if (listener != null) {
                    listener.onDisconnected(new EMDevice(message.id));
                }
            }
        } else {
            switch (message.mRecvMsg.msgType) {
                case Header.MsgType.PAYLOAD:
                    for (EMMessageListener listener : EMMessageManager.getInstance().getListener()) {
                        if (listener != null) {
                            PayloadProto.Payload payload = (PayloadProto.Payload) message.mRecvMsg.data;
                            EMMessage emMessage = new EMMessage(message.id, payload.getContent());
                            listener.onMessageReceived(emMessage);
                        }
                    }
                    break;
            }
        }
    }

}
