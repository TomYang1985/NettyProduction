package com.netty.client.msg;

/**
 * Created by robincxiao on 2017/9/26.
 */

public class EMResourceRate extends EMMessage{
    public String resourceRate;

    public EMResourceRate(String resourceRate) {
        msgType = MSG_TYPE_RESOURCE_RATE;
        this.resourceRate = resourceRate;
    }
}
