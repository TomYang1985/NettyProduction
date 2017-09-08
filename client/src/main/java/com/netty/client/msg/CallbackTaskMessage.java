package com.netty.client.msg;

/**
 * Created by robincxiao on 2017/9/1.
 */

public class CallbackTaskMessage {
    public static final int MSG_TYPE_ACTIVE = 1;
    public static final int MSG_TYPE_INACTIVE = 2 ;
    public static final int MSG_TYPE_NOT_WIFI = 3;//当前Wifi未连接
    public static final int MSG_TYPE_CONNECTING = 4;//正在连接
    public static final int MSG_TYPE_CONNECTED = 5;//已连接
    public static final int MSG_TYPE_HOST_NULL = 6;//host为null
    public static final int MSG_TYPE_CONNECT_FAIL = 7;//连接失败
    public static final int MSG_TYPE_CONNECT_SUCC_BY_USER = 8;//用户点击连接成功
    public int type;
    public RecvMessage recvMessage;
    public String from;
}
