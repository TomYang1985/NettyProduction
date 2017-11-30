package com.netty.client.innermsg;

import com.netty.client.innermsg.NettyMessage;

/**
 * Created by robincxiao on 2017/9/1.
 */

public class CallbackMessage {
    public static final int MSG_TYPE_RECV_MSG = 0;//业务消息
    public static final int MSG_TYPE_RECONNECT = 1;//设备正在重连
    public static final int MSG_TYPE_DISCONNECT = 2 ;//设备断开连接
    public static final int MSG_TYPE_NOT_CONNECT_WIFI = 3;//未连接Wifi
    public static final int MSG_TYPE_CONNECTING = 4;//正在连接
    public static final int MSG_TYPE_CONNECTED = 5;//已连接
    public static final int MSG_TYPE_HOST_NULL = 6;//host为null
    public static final int MSG_TYPE_CONNECT_FAIL = 7;//连接失败
    public int type;
    public String from;//服务器host
    public NettyMessage recvMessage;
}
