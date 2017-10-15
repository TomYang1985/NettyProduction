package com.netty.client.msg;

/**
 * Created by xiaoguochang on 2017/9/17.
 */

public class EMAppUpdate extends EMMessage {
    public String packageName;
    public String appName;
    public int versionCode;
    public String versionName;
    public boolean isSystem;
    public String iconUrl;
    public long size;

    public EMAppUpdate(String packageName, String appName, int versionCode, String versionName, boolean isSystem, String iconUrl, long size) {
        msgType = MSG_TYPE_APP_UPDATE;
        this.packageName = packageName;
        this.appName = appName;
        this.versionCode = versionCode;
        this.versionName = versionName;
        this.isSystem = isSystem;
        this.iconUrl = iconUrl;
        this.size = size;
    }
}
