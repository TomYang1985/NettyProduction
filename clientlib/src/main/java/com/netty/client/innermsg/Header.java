package com.netty.client.innermsg;

/**
 * Created by robincxiao on 2017/8/23.
 */

public class Header {
    public static final byte PROTOCOL_VERSION = 1;

    /**
     * 消息类型
     */
    public class MsgType{
        public static final byte PING = 1;
        public static final byte PONG = 2;
        public static final byte ACK = 3;
        public static final byte EXCHANGE_KEY = 4;//交换key
        public static final byte EXCHANGE_KEY_RESP = 5;//交换key响应
        public static final byte REQUEST = 6;//业务请求
        public static final byte RESPONSE = 7;//业务返回

        public static final byte PAYLOAD = (byte) 200;//透传消息
    }

    /**
     * 具体业务类型
     */
    public class BusinessType{
        public static final byte REQUEST_TV_UPDATE = 1;//更新TV端
        public static final byte REQUEST_APP_LIST = 2;//已安装应用列表请求
        public static final byte REQUEST_CLEAN = 3;//清理
        public static final byte REQUEST_OPEN_APP = 4;//打开APP
        public static final byte REQUEST_REMOVE_APP = 5;//删除APP
        public static final byte REQUEST_UPDATE_APP = 6;//更新APP
        public static final byte REQUEST_DOWNLOAD_APP = 7;//下载APP
        public static final byte REQUEST_OPEN_SETTING = 8;//打开系统设置页面
        public static final byte REQUEST_RESOURCE_RATE = 9;//资源占用率
        public static final byte REQUEST_DEVICE_INFO = 10;//设备信息

        public static final byte RESPONSE_APP_ADDED = 1;//APP安装
        public static final byte RESPONSE_APP_REMOVED = 2;//APP卸载
        public static final byte RESPONSE_APP_LIST = 3;//已安装应用列表响应
        public static final byte RESPONSE_CLEAN = 4;//清理返回
        public static final byte RESPONSE_RESOURCE_RATE = 5;//资源占用率返回
        public static final byte RESPONSE_DEVICE_INFO = 6;//设备信息
        public static final byte RESPONSE_DOWNLOAD = 7;//下载返回
    }
}
