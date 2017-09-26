package com.tencent.tvmanager.netty.core;

import android.text.TextUtils;

import com.tencent.tvmanager.netty.core.threadpool.ExecutorFactory;
import com.tencent.tvmanager.netty.core.threadpool.MessageSendTask;
import com.tencent.tvmanager.netty.innermsg.CleanResponseProto;
import com.tencent.tvmanager.netty.innermsg.Header;
import com.tencent.tvmanager.netty.innermsg.NettyMessage;
import com.tencent.tvmanager.netty.innermsg.PayloadProto;
import com.tencent.tvmanager.netty.listener.EMMessageListener;
import com.tencent.tvmanager.netty.util.MID;
import com.tencent.tvmanager.util.L;

import java.util.ArrayList;
import java.util.List;

import io.netty.channel.Channel;

/**
 * Created by robincxiao on 2017/9/1.
 */

public class EMMessageManager {
    private volatile static EMMessageManager sInstance;
    private List<EMMessageListener> mListeners;

    private EMMessageManager() {
        mListeners = new ArrayList<>();
    }

    public static EMMessageManager getInstance() {
        if (sInstance == null) {
            synchronized (EMMessageManager.class) {
                if (sInstance == null) {
                    sInstance = new EMMessageManager();
                }
            }
        }

        return sInstance;
    }

    public List<EMMessageListener> getListener() {
        return mListeners;
    }

    public void addListener(EMMessageListener listener) {
        mListeners.add(listener);
    }

    public void removeListener(EMMessageListener listener) {
        mListeners.remove(listener);
    }

    /**
     * 发送payload信息
     *
     * @param content
     */
    public void sendPayload(String id, String content) {
        if (TextUtils.isEmpty(id)) {
            L.print("sendPayload id == null");
            return;
        }

        if (TextUtils.isEmpty(content)) {
            L.print("sendPayload content == null");
            return;
        }

        Channel channel = EMConnectManager.getInstance().getChannelGroup().get(id);
        if (channel != null) {
            PayloadProto.Payload body = PayloadProto.Payload.newBuilder()
                    .setMessageId(MID.getId()).setContent(content)
                    .build();
            NettyMessage message = new NettyMessage();
            message.msgType = Header.MsgType.PAYLOAD;
            message.body = body;
            ExecutorFactory.submitSendTask(new MessageSendTask(channel, message));
        } else {
            L.print("mChannelGroup is not contains id = " + id);
        }
    }

    /**
     * 垃圾清理返回
     *
     * @param id
     * @param code
     * @param sdkErrCode
     * @param memRubbishSize
     * @param sysRubbishSize
     * @param cacheRubbishSize
     * @param apkRubbishSize
     */
    public void sendCleanResponse(String id, int code, int sdkErrCode, long memRubbishSize, long sysRubbishSize
            , long cacheRubbishSize, long apkRubbishSize) {
        if (TextUtils.isEmpty(id)) {
            L.print("sendCleanResponse id == null");
            return;
        }

        Channel channel = EMConnectManager.getInstance().getChannelGroup().get(id);
        if (channel != null) {
            CleanResponseProto.CleanResponse body = CleanResponseProto.CleanResponse.newBuilder()
                    .setMessageId(MID.getId())
                    .setCode(code)
                    .setSdkCode(sdkErrCode)
                    .setMemRubbish(memRubbishSize)
                    .setSysRubbish(sysRubbishSize)
                    .setCacheRubbish(cacheRubbishSize)
                    .setApkRubbish(apkRubbishSize).build();
            NettyMessage message = new NettyMessage();
            message.msgType = Header.MsgType.RESPONSE;
            message.businessType = Header.BusinessType.RESPONSE_CLEAN;
            message.body = body;
            ExecutorFactory.submitSendTask(new MessageSendTask(channel, message));
        } else {
            L.print("mChannelGroup is not contains id = " + id);
        }
    }
}
