package com.netty.client.msg;

/**
 * Created by robincxiao on 2017/9/1.
 */

public class EMMessage {
    public static final int MSG_TYPE_PAYLOAD = 1;//测试用
    public static final int MSG_TYPE_UPDATE = 2;
    public static final int MSG_TYPE_APP_ADDED = 3;//安装APP
    public static final int MSG_TYPE_APP_REMOVED = 4;//卸载APP
    public static final int MSG_TYPE_APP_LIST = 5;//已安装应用列表
    public static final int MSG_TYPE_RUBBISH= 6;//垃圾扫描结果
    public static final int MSG_TYPE_RESOURCE_RATE= 7;//资源占用率

    public int msgType;
}
