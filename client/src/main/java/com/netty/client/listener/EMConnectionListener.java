package com.netty.client.listener;

/**
 * Created by robincxiao on 2017/9/1.
 */

public interface EMConnectionListener {
    /**
     * 连接成功(用户主动连接)
     *
     * @param id 服务器id(即host)
     */
    void onConnectSuccByUser(String id);

    /**
     * 数据channel校验成功
     *
     * @param id
     */
    void onChannelCheckSucc(String id);

    /**
     * 连接成功(用户主动连接和自动恢复连接)
     *
     * @param id 服务器id(即host)
     */
    void onActive(String id);

    /**
     * 连接断开
     *
     * @param id 服务器id(即host)
     */
    void onInActive(String id);

    /**
     * 连接错误
     *
     * @param type
     */
    void onError(int type);
}
