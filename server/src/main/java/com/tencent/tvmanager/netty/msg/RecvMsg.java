package com.tencent.tvmanager.netty.msg;

import com.google.protobuf.MessageLite;

/**
 * Created by robincxiao on 2017/8/23.
 */

public class RecvMsg {
    public byte msgType;
    public MessageLite data;
}
