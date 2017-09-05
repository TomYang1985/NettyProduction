package com.netty.server.msg;

/**
 * Created by robincxiao on 2017/9/1.
 */

public class EMCallbackTaskMessage {
    public static final int MSG_TYPE_ACTIVE = 1;
    public static final int MSG_TYPE_INACTIVE = 2 ;
    public int type;
    public String id;
    public RecvMsg mRecvMsg;

    public EMCallbackTaskMessage(int type) {
        this.type = type;
    }

    public EMCallbackTaskMessage(RecvMsg recvMsg) {
        this.mRecvMsg = recvMsg;
    }
}
