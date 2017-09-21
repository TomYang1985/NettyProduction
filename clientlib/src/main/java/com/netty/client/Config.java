package com.netty.client;

import android.os.Environment;

/**
 * Created by robincxiao on 2017/8/23.
 */

public class Config {
    public static final String TAG = "Netty";
    public static boolean isDebug = true;// 是否需要打印log

    public static final String DIR_BASE = "/com.netty.client/";
    //logo文件目录
    public static final String DIR_LOG = DIR_BASE + "log";
    //log过期天数
    public static final int LOG_RETENTION_PERIOD = 7;

    public static String getLogPath(){
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory() + DIR_LOG;
        }else {
            return "/data/data/com.netty.client/log";
        }
    }
}
