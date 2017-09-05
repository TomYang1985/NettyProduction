package com.netty.server.core;

import android.text.TextUtils;

import com.netty.server.core.threadpool.ExecutorFactory;
import com.netty.server.core.threadpool.MessageSendTask;
import com.netty.server.listener.EMMessageListener;
import com.netty.server.msg.Header;
import com.netty.server.msg.PayloadProto;
import com.netty.server.msg.SendMsg;
import com.netty.server.utils.HostUtils;
import com.netty.server.utils.L;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.Channel;

/**
 * Created by robincxiao on 2017/9/1.
 */

public class EMMessageManager {
    private volatile static EMMessageManager sInstance;
    private List<EMMessageListener> mListeners;
    private ConcurrentHashMap<String, Channel> mChannelGroup;

    private EMMessageManager() {
        mListeners = new ArrayList<>();
        mChannelGroup = new ConcurrentHashMap<>();
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

    public List<EMMessageListener> getListener() {
        return mListeners;
    }

    public void addListener(EMMessageListener listener) {
        mListeners.add(listener);
    }

    public void removeListener(EMMessageListener listener) {
        mListeners.remove(listener);
    }

    public ConcurrentHashMap<String, Channel> getChannelGroup() {
        return mChannelGroup;
    }

    public void addChannel(Channel channel) {
        String id = HostUtils.parseHostPort(channel.remoteAddress().toString());
        if(!mChannelGroup.containsKey(id) && !TextUtils.isEmpty(id)) {
            mChannelGroup.put(id, channel);
        }
    }

    public void removeChannel(Channel channel) {
        String id = HostUtils.parseHostPort(channel.remoteAddress().toString());
        if(mChannelGroup.containsKey(id) && !TextUtils.isEmpty(id)) {
            mChannelGroup.remove(id);
        }
    }

    /**
     * 发送payload信息
     *
     * @param content
     */
    public void sendPayload(String id, String content) {
        if (TextUtils.isEmpty(id)) {
            L.print("sendPayload id == null");
            return;
        }

        if (TextUtils.isEmpty(content)) {
            L.print("sendPayload content == null");
            return;
        }

        Channel channel = mChannelGroup.get(id);
        if (channel != null) {
            PayloadProto.Payload payload = PayloadProto.Payload.newBuilder()
                    .setContent(content).build();
            ExecutorFactory.submitSendTask(new MessageSendTask(channel, new SendMsg(Header.MsgType.PAYLOAD, payload)));
        } else {
            L.print("mChannelGroup is not contains id = " + id);
        }
    }
}
