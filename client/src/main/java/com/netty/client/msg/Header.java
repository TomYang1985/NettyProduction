package com.netty.client.msg;

/**
 * Created by robincxiao on 2017/8/23.
 */

public class Header {
    public static final byte PROTOCOL_VERSION = 1;

    /**
     * 消息类型
     */
    public class MsgType{
        public static final byte COMMOND = 1;//控制消息
        public static final byte PAYLOAD = 2;//透传消息

        public static final byte PING = 100;
        public static final byte PONG = 101;
        public static final byte ACK = 102;
    }

    /**
     * 具体业务类型
     */
    public class BusynessType{

    }

    public static final byte COMMOND = 1;//控制消息
    public static final byte PAYLOAD = 2;//透传消息

    public static final byte PING = 100;
    public static final byte PONG = 101;
    public static final byte ACK = 102;
}
