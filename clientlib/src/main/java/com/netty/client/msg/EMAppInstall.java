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

    public EMAppInstall(String packageName, String appName, int versionCode, String versionName, boolean isSystem) {
        msgType = MSG_TYPE_APP_ADDED;
        this.packageName = packageName;
        this.appName = appName;
        this.versionCode = versionCode;
        this.versionName = versionName;
        this.isSystem = isSystem;
    }
}
