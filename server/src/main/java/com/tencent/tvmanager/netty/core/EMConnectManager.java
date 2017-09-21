package com.tencent.tvmanager.netty.core;


import android.text.TextUtils;

import com.tencent.tvmanager.netty.listener.EMConnectionListener;
import com.tencent.tvmanager.netty.msg.EMDevice;
import com.tencent.tvmanager.netty.util.HostUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.Channel;

/**
 * Created by robincxiao on 2017/9/1.
 */

public class EMConnectManager {
    private volatile static EMConnectManager sInstance;
    private List<EMConnectionListener> mListeners;
    private ConcurrentHashMap<String, Channel> mChannelGroup;//管理连接的客户端

    private EMConnectManager() {
        mListeners = new ArrayList<>();
        mChannelGroup = new ConcurrentHashMap<>();
    }

    public List<EMConnectionListener> getListener() {
        return mListeners;
    }

    public static EMConnectManager getInstance() {
        if (sInstance == null) {
            synchronized (EMConnectManager.class) {
                if (sInstance == null) {
                    sInstance = new EMConnectManager();
                }
            }
        }

        return sInstance;
    }

    public void addListener(EMConnectionListener listener) {
        mListeners.add(listener);
        //添加监听时，检测当前已连接的客户端，如果有则返回已连接的客户端
        if (mChannelGroup.size() > 0) {
            List<EMDevice> list = new ArrayList<>();
            Iterator<String> keys = mChannelGroup.keySet().iterator();
            while (keys.hasNext()) {
                list.add(new EMDevice(keys.next()));
            }

            listener.onConnected(list);
        }
    }

    public void removeListener(EMConnectionListener listener) {
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
}
