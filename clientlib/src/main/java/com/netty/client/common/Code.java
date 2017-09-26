package com.netty.client.common;

/**
 * Created by robincxiao on 2017/9/7.
 */

public class Code {
    //客户端错误码
    public static final int CODE_NOT_WIFI = 1;//当前Wifi未连接
    public static final int CODE_CONNECTING = 2;//正在连接
    public static final int CODE_CONNECTED = 3;//已连接
    public static final int CODE_HOST_NULL = 4;//host为null
    public static final int CODE_CONNECT_FAIL = 5;//连接失败
    public static final int CODE_CONNECT_SUCC_BY_USER = 6;//用户点击连接成功
    //服务端错误码
    public static final int RESULT_OK = 200;
    public static final int CODE_CLEAN_ERROR = 1000;//清理错误
    public static final int CODE_CLEAN_CANCEL = 1001;//清理取消
}
