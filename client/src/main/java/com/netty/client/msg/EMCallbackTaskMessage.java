package com.netty.client.msg;

/**
 * Created by robincxiao on 2017/9/1.
 */

public class EMCallbackTaskMessage {
    public static final int MSG_TYPE_ACTIVE = 1;
    public static final int MSG_TYPE_INACTIVE = 2 ;
    public int type;
    public RecvMsg mRecvMsg;
    public String from;
}
