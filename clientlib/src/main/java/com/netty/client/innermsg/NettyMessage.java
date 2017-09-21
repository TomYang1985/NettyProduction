package com.netty.client.innermsg;

import com.google.protobuf.MessageLite;

/**
 * Created by robincxiao on 2017/9/15.
 */

public class NettyMessage extends BaseMessage{
    public MessageLite body;
    public String from;//服务器host
}
