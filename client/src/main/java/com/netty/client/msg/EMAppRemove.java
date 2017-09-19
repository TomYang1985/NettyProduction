package com.netty.client.msg;

/**
 * Created by xiaoguochang on 2017/9/17.
 */

public class EMAppRemove extends EMMessage {
    public String packageName;

    public EMAppRemove(String packageName) {
        msgType = MSG_TYPE_APP_REMOVED;
        this.packageName = packageName;
    }
}
