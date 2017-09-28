package com.netty.client.msg;

/**
 * Created by xiaoguochang on 2017/9/17.
 */

public class EMDownload extends EMMessage {
    public int code;
    public String url;

    public EMDownload(int code, String url) {
        msgType = MSG_TYPE_DOWNLOAD;
        this.code = code;
        this.url = url;
    }

}
