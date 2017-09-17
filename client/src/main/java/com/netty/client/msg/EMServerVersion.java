package com.netty.client.msg;

/**
 * Created by xiaoguochang on 2017/9/17.
 */

public class EMServerVersion extends EMMessage{
    public int versionCode;
    public String versionName;

    public EMServerVersion(int versionCode, String versionName){
        msgType = MSG_TYPE_SERVER_VERSION;
        this.versionCode = versionCode;
        this.versionName = versionName;
    }
}
