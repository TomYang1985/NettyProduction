package com.netty.client.common;

/**
 * Created by robincxiao on 2017/9/7.
 */

public class Code {
    //客户端错误码
    public static final int CODE_NOT_WIFI = 1;//当前Wifi未连接
    public static final int CODE_CONNECTING = 2;//正在连接
    public static final int CODE_CONNECTED = 3;//已连接
    public static final int CODE_DISCONNECT = 4;//连接断开
    public static final int CODE_HOST_NULL = 5;//host为null
    public static final int CODE_CONNECT_FAIL = 6;//连接失败
    public static final int CODE_CONNECT_SUCC_BY_USER = 7;//用户点击连接成功
    //服务端错误码
    public static final int RESULT_OK = 200;
    public static final int CODE_CLEAN_ERROR = 1000;//清理错误
    public static final int CODE_CLEAN_CANCEL = 1001;//清理取消
    public static final int CODE_DOWNLOAD_PENDING = 1002;//下载等待
    public static final int CODE_DOWNLOAD_STARTED = 1003;//下载开始
    public static final int CODE_DOWNLOAD_FAIL = 1004;//下载失败
}
