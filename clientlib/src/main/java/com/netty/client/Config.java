package com.netty.client;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Environment;

import com.netty.client.core.EMClient;
import com.netty.client.utils.AppUtils;
import com.netty.client.utils.L;

import java.util.List;

/**
 * Created by robincxiao on 2017/8/23.
 */

public class Config {
    public static final String TAG = "Netty";
    public static boolean isDebug = true;// 是否需要打印log

    //log过期天数
    public static final int LOG_RETENTION_PERIOD = 7;

    /**
     * 获取日志文件路径
     *
     * @return
     */
    public static String getLogPath() {
        Context context = EMClient.getInstance().getContext();
        L.print("getLogPath=" + getCurrentProcessName(context));
        String logPath = "";
        String packageName = "com.anguanjia.security";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            if (context != null) {
                try {
                    logPath = context.getExternalFilesDir("xlog").getAbsolutePath();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    logPath = Environment.getExternalStorageDirectory() + "/Android/data/" + packageName + "/files/xlog";
                }
            } else {
                logPath = Environment.getExternalStorageDirectory() + "/Android/data/" + packageName + "/files/xlog";
            }
        } else {
            if (context != null) {
                logPath = context.getFilesDir().getAbsolutePath() + "/xlog";
            } else {
                logPath = "/data/data/" + packageName + "/files/xlog";
            }
        }

        return logPath;
    }

    public static String getCurrentProcessName(Context cxt) {
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        if (am != null) {
            List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
            if (runningApps == null) {
                return "";
            }
            int pid = android.os.Process.myPid();
            for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
                if (procInfo.pid == pid) {
                    return procInfo.processName;
                }
            }
        }

        return "";
    }
}
