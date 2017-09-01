package com.netty.client.core;

import com.netty.client.listener.EMConnectionListener;

import java.util.ArrayList;
import java.util.List;

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

    public void addListener(EMConnectionListener listener){
        mListeners .add(listener);
    }

    public void removeListener(EMConnectionListener listener){
        mListeners.add(listener);
    }
}
