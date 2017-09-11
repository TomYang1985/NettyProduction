package com.tencent.tvmanager.netty.core;


import com.tencent.tvmanager.netty.listener.EMConnectionListener;
import com.tencent.tvmanager.netty.msg.EMDevice;

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

    private EMConnectManager() {
        mListeners = new ArrayList<>();
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
        ConcurrentHashMap<String, Channel> channelGroups = EMMessageManager.getInstance().getChannelGroup();
        if (channelGroups.size() > 0) {
            List<EMDevice> list = new ArrayList<>();
            Iterator<String> keys = channelGroups.keySet().iterator();
            while (keys.hasNext()) {
                list.add(new EMDevice(keys.next()));
            }

            listener.onConnected(list);
        }
    }

    public void removeListener(EMConnectionListener listener) {
        mListeners.remove(listener);
    }


}
