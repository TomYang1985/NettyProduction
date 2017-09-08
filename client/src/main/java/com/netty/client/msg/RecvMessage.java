package com.netty.client.msg;

import com.google.protobuf.MessageLite;

/**
 * Created by robincxiao on 2017/8/23.
 */

public class RecvMessage {
    public byte msgType;
    public MessageLite data;
}
