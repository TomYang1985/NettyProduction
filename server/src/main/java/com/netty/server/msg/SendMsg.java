package com.netty.server.msg;

import com.google.protobuf.MessageLite;

/**
 * Created by xiaoguochang on 2017/8/27.
 */

public class SendMsg {
    public byte msgType;
    public MessageLite data;

    public SendMsg(byte msgType){
        this.msgType = msgType;
    }

    public SendMsg(byte msgType, MessageLite data) {
        this.msgType = msgType;
        this.data = data;
    }
}
