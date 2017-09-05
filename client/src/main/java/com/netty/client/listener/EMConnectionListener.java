package com.netty.client.listener;

import com.netty.client.multicast.EMDevice;

/**
 * Created by robincxiao on 2017/9/1.
 */

public interface EMConnectionListener {
    /**
     * 连接成功
     * @param id 服务器id(即host)
     */
    void onConnected(String id);

    /**
     * 连接断开
     * @param id 服务器id(即host)
     */
    void onDisconnected(String id);
}
