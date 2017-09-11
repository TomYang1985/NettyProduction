package com.tencent.tvmanager.netty.core.threadpool;


import com.tencent.tvmanager.netty.core.EMConnectManager;
import com.tencent.tvmanager.netty.core.EMMessageManager;
import com.tencent.tvmanager.netty.listener.EMConnectionListener;
import com.tencent.tvmanager.netty.listener.EMMessageListener;
import com.tencent.tvmanager.netty.msg.EMCallbackTaskMessage;
import com.tencent.tvmanager.netty.msg.EMDevice;
import com.tencent.tvmanager.netty.msg.EMMessage;
import com.tencent.tvmanager.netty.msg.Header;
import com.tencent.tvmanager.netty.msg.PayloadProto;

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
