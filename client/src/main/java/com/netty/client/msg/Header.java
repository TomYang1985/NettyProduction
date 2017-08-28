package com.netty.client.msg;

/**
 * Created by robincxiao on 2017/8/23.
 */

public class Header {
    public static final byte PROTOCOL_VERSION = 1;
    /** Request */
    public static final byte REQUEST = 1;
    /** Response */
    public static final byte RESPONSE = 2;
    public static final byte CHAT_MSG = 3;//聊天消息

    public static final byte PING = 100;
    public static final byte PONG = 101;
    public static final byte ACK = 102;
}
