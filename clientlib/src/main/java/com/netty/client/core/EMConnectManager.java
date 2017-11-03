package com.netty.client.core;

import com.netty.client.listener.EMConnectionListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by robincxiao on 2017/9/1.
 */

public class EMConnectManager {
    private volatile static EMConnectManager sInstance;
    private CopyOnWriteArrayList<EMConnectionListener> mListeners;

    private EMConnectManager() {
        mListeners = new CopyOnWriteArrayList<>();
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
        if(listener != null) {
            mListeners.add(listener);
        }
    }

    public void removeListener(EMConnectionListener listener){
        if(listener != null) {
            mListeners.remove(listener);
        }
    }
}
