package com.netty.client;

import android.content.Context;
import android.os.Environment;

import com.netty.client.core.EMClient;
import com.netty.client.utils.L;

/**
 * Created by robincxiao on 2017/8/23.
 */

public class Config {
    public static final String TAG = "Netty";
    public static boolean isDebug = false;// 是否需要打印log

    public static final String DIR_BASE = "/com.netty.client/";
    //logo文件目录
    public static final String DIR_LOG = DIR_BASE + "log";
    //log过期天数
    public static final int LOG_RETENTION_PERIOD = 7;

    /**
     * 获取日志文件路径
     * @return
     */
    public static String getLogPath(){
        Context context = EMClient.getInstance().getContext();
        String logPath = "";
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            if(context != null) {
                logPath = context.getExternalFilesDir("xlog").getAbsolutePath();
            }else {
                logPath = Environment.getExternalStorageDirectory() + "/Android/com.tencent.tvmanager/files/xlog";
            }
        }else {
            if(context != null) {
                logPath = context.getFilesDir().getAbsolutePath();
            }else {
                logPath = "/data/data/com.tencent.tvmanager/files/xlog";
            }
        }
        L.d(logPath);
        return logPath;
    }
}
