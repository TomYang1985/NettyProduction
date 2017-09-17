package com.tencent.tvmanager.netty.core.threadpool;


import com.tencent.tvmanager.netty.core.EMConnectManager;
import com.tencent.tvmanager.netty.core.EMMessageManager;
import com.tencent.tvmanager.netty.listener.EMConnectionListener;
import com.tencent.tvmanager.netty.listener.EMMessageListener;
import com.tencent.tvmanager.netty.innermsg.CallbackMessage;
import com.tencent.tvmanager.netty.msg.EMDevice;
import com.tencent.tvmanager.netty.msg.EMMessage;
import com.tencent.tvmanager.netty.innermsg.Header;
import com.tencent.tvmanager.netty.msg.EMPayload;
import com.tencent.tvmanager.netty.msg.PayloadProto;

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
        if (message.type == CallbackMessage.MSG_TYPE_ACTIVE) {
            for (EMConnectionListener listener : EMConnectManager.getInstance().getListener()) {
                if (listener != null) {
                    listener.onConnected(new EMDevice(message.id));
                }
            }
        } else if (message.type == CallbackMessage.MSG_TYPE_INACTIVE) {
            for (EMConnectionListener listener : EMConnectManager.getInstance().getListener()) {
                if (listener != null) {
                    listener.onDisconnected(new EMDevice(message.id));
                }
            }
        } else if (message.type == CallbackMessage.MSG_TYPE_RECV_MSG){
            switch (message.recvMsg.msgType) {
                case Header.MsgType.PAYLOAD:
                    PayloadProto.Payload body = (PayloadProto.Payload) message.recvMsg.body;
                    EMPayload payload = new EMPayload(message.id, body.getContent());
                    callbackMessage(payload);
                    break;
            }
        }
    }

    /**
     * 消息回调
     * @param message
     */
    private void callbackMessage(EMMessage message){
        for (EMMessageListener listener : EMMessageManager.getInstance().getListener()) {
            if (listener != null) {
                listener.onMessageReceived(message);
            }
        }
    }
}
