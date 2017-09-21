package com.netty.client.innermsg;

import com.netty.client.innermsg.NettyMessage;

/**
 * Created by robincxiao on 2017/9/1.
 */

public class CallbackMessage {
    public static final int MSG_TYPE_RECV_MSG = 0;
    public static final int MSG_TYPE_ACTIVE = 1;
    public static final int MSG_TYPE_INACTIVE = 2 ;
    public static final int MSG_TYPE_NOT_WIFI = 3;//当前Wifi未连接
    public static final int MSG_TYPE_CONNECTING = 4;//正在连接
    public static final int MSG_TYPE_CONNECTED = 5;//已连接
    public static final int MSG_TYPE_HOST_NULL = 6;//host为null
    public static final int MSG_TYPE_CONNECT_FAIL = 7;//连接失败
    public static final int MSG_TYPE_CONNECT_SUCC_BY_USER = 8;//用户点击连接成功
    public int type;
    public String from;//服务器host
    public NettyMessage recvMessage;
}
