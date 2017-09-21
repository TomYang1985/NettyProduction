package com.tencent.tvmanager.netty.innermsg;

/**
 * Created by robincxiao on 2017/8/23.
 */

public class Header {
    public static final byte PROTOCOL_VERSION = 1;

    /**
     * 消息类型
     */
    public class MsgType {
        public static final byte COMMOND = 1;//控制消息
        public static final byte PAYLOAD = 2;//透传消息

        public static final byte PING = 100;
        public static final byte PONG = 101;
        public static final byte ACK = 102;
        public static final byte EXCHANGE_KEY = 103;//交换key
        public static final byte EXCHANGE_KEY_RESP = 104;//交换key响应
        public static final byte REQUEST = 105;//业务请求
        public static final byte RESPONSE = 106;//业务返回
    }

    /**
     * 具体业务类型
     */
    public class BusinessType {
        public static final byte REQUEST_TV_UPDATE = 1;//更新TV端
        public static final byte REQUEST_APP_LIST = 2;//请求已安装应用列表

        public static final byte RESPONSE_APP_ADDED = 1;//APP安装
        public static final byte RESPONSE_APP_REMOVED = 2;//APP卸载
        public static final byte RESPONSE_APP_LIST = 3;//已安装应用列表响应
    }
}
