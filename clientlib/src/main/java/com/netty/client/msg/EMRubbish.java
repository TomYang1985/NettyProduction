package com.netty.client.msg;

/**
 * Created by xiaoguochang on 2017/9/17.
 */

public class EMRubbish extends EMMessage {
    public int code;
    public int sdkCode;
    public long memRubbish;
    public long sysRubbish;
    public long unInstallRubbish;
    public long cacheRubbish;

    public EMRubbish(int code, int sdkCode, long memRubbish, long sysRubbish, long unInstallRubbish, long cacheRubbish) {
        msgType = MSG_TYPE_RUBBISH;
        this.code = code;
        this.sdkCode = sdkCode;
        this.memRubbish = memRubbish;
        this.sysRubbish = sysRubbish;
        this.unInstallRubbish = unInstallRubbish;
        this.cacheRubbish = cacheRubbish;
    }

}
