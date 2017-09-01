package com.netty.client.listener;

/**
 * Created by robincxiao on 2017/9/1.
 */

public interface EMConnectionListener {
    void onConnected();

    void onDisconnected();
}
