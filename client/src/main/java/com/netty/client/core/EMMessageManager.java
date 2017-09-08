package com.netty.client.core;

import android.text.TextUtils;

import com.netty.client.core.threadpool.ExecutorFactory;
import com.netty.client.core.threadpool.MessageSendTask;
import com.netty.client.listener.EMMessageListener;
import com.netty.client.msg.Header;
import com.netty.client.msg.PayloadProto;
import com.netty.client.msg.SendMessage;
import com.netty.client.utils.L;

import java.util.ArrayList;
import java.util.List;

import io.netty.channel.Channel;

/**
 * Created by robincxiao on 2017/9/1.
 */

public class EMMessageManager {
    private volatile static EMMessageManager sInstance;
    private List<EMMessageListener> mListeners;
    private Channel mChannel;

    private EMMessageManager() {
        mListeners = new ArrayList<>();
    }

    public static EMMessageManager getInstance() {
        if (sInstance == null) {
            synchronized (EMMessageManager.class) {
                if (sInstance == null) {
                    sInstance = new EMMessageManager();
                }
            }
        }

        return sInstance;
    }

    public void setChannel(Channel channel) {
        this.mChannel = channel;
    }

    public List<EMMessageListener> getListener() {
        return mListeners;
    }

    public void addListener(EMMessageListener listener) {
        mListeners.add(listener);
    }

    public void removeListener(EMMessageListener listener) {
        mListeners.remove(listener);
    }

    /**
     * 发送payload信息
     * @param content
     */
    public void sendPayload(String content) {
        if (mChannel == null) {
            L.print("sendPayload mChannel == null");
            return;
        }

        if (!mChannel.isActive() || !mChannel.isWritable()) {
            L.print("sendPayload mChannel not active or not writable");
            return;
        }

        if (TextUtils.isEmpty(content)) {
            L.print("sendPayload content == null");
            return;
        }

        PayloadProto.Payload payload = PayloadProto.Payload.newBuilder()
                .setContent(content).build();
        ExecutorFactory.submitSendTask(new MessageSendTask(mChannel, new SendMessage(Header.MsgType.PAYLOAD, payload)));
    }
}
