package com.netty.client.listener;

/**
 * Created by robincxiao on 2017/9/1.
 */

public interface EMConnectionListener {
    /**
     * 连接成功，该接口回调的场景有如下几种情况：
     * 1.首次连接成功时；
     * 2.断线后自动重连成功后；
     *
     */
    void onConnect();

    /**
     * 连接断开（从已连接到未连接的回调）
     *
     */
    void onDisconnect();

    /**
     * 正在尝试重连，该接口回调的场景有如下几种情况：
     * 1.服务端校验不合法时，开始自动重连前被调用，断线后，开始自动重连前被调用；
     * 2.其它原因从已连接状态到断开连接时，开始自动重连前被调用；
     *
     */
    void onReconnect();

    /**
     * 连接错误
     *
     * @param type
     */
    void onError(int type);
}
