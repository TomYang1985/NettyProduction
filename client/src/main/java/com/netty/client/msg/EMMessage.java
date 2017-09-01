package com.netty.client.msg;

/**
 * Created by robincxiao on 2017/9/1.
 */

public class EMMessage {
    public String from;
    public String content;

    public EMMessage(String from, String content) {
        this.from = from;
        this.content = content;
    }
}
