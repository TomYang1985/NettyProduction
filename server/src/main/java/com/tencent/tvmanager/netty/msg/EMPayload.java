package com.tencent.tvmanager.netty.msg;

/**
 * Created by xiaoguochang on 2017/9/17.
 */

public class EMPayload extends EMMessage{
    public String from;
    public String content;

    public EMPayload(String from, String content){
        msgType = MSG_TYPE_PAYLOAD;
        this.from = from;
        this.content = content;
    }
}
