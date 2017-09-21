package com.netty.client.msg;

/**
 * Created by xiaoguochang on 2017/9/17.
 */

public class EMUpdate extends EMMessage {
    public static final int UPDATE_TYPE_TV = 1;//TV端需要更新
    public static final int UPDATE_TYPE_PHONE = 2;//手机端需要更新
    public int updateType;

    public EMUpdate(int updateType) {
        msgType = MSG_TYPE_UPDATE;
        this.updateType = updateType;
    }
}
