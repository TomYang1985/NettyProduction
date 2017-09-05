package com.netty.server.listener;


import com.netty.server.msg.EMMessage;

/**
 * Created by robincxiao on 2017/9/1.
 */

public interface EMMessageListener {
    //收到消息
    void onMessageReceived(EMMessage message);
    //收到透传消息
    //void onCmdMessageReceived(EMMessage message);
}
