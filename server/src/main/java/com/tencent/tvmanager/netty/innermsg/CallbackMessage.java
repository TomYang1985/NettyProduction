package com.tencent.tvmanager.netty.innermsg;

import com.tencent.tvmanager.netty.innermsg.NettyMessage;

/**
 * Created by robincxiao on 2017/9/1.
 */

public class CallbackMessage {
    public static final int MSG_TYPE_RECV_MSG = 0;
    public static final int MSG_TYPE_ACTIVE = 1;
    public static final int MSG_TYPE_INACTIVE = 2 ;
    public int type;
    public String id;
    public NettyMessage recvMsg;

    public CallbackMessage() {
    }

    public CallbackMessage(int type) {
        this.type = type;
    }

}
