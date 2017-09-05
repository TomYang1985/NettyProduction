package com.netty.server.listener;

import com.netty.server.msg.EMDevice;

import java.util.List;

/**
 * Created by robincxiao on 2017/9/1.
 */

public interface EMConnectionListener {
    /**
     * 某个设备连接
     * @param device
     */
    void onConnected(EMDevice device);

    /**
     * 某个设备断开
     * @param device
     */
    void onDisconnected(EMDevice device);

    /**
     * 已连接的设备列表
     * @param devices
     */
    void onConnected(List<EMDevice> devices);
}
