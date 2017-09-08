package com.netty.client.msg;

import com.google.protobuf.MessageLite;

/**
 * Created by xiaoguochang on 2017/8/27.
 */

public class SendMessage {
    public byte msgType;
    public MessageLite data;

    public SendMessage(byte msgType){
        this.msgType = msgType;
    }

    public SendMessage(byte msgType, MessageLite data) {
        this.msgType = msgType;
        this.data = data;
    }
}
