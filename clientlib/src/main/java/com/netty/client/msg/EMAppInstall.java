package com.netty.client.msg;

/**
 * Created by xiaoguochang on 2017/9/17.
 */

public class EMAppInstall extends EMMessage {
    public String packageName;
    public String appName;
    public int versionCode;
    public String versionName;
    public boolean isSystem;
    public String iconUrl;
    public long size;
    public long firstInstallTime;

    public EMAppInstall(String packageName, String appName, int versionCode, String versionName, boolean isSystem,
                        String iconUrl, long size, long firstInstallTime) {
        msgType = MSG_TYPE_APP_ADDED;
        this.packageName = packageName;
        this.appName = appName;
        this.versionCode = versionCode;
        this.versionName = versionName;
        this.isSystem = isSystem;
        this.iconUrl = iconUrl;
        this.size = size;
        this.firstInstallTime = firstInstallTime;
    }
}
