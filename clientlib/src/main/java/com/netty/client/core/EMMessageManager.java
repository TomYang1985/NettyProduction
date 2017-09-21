package com.netty.client.core;

import com.netty.client.core.threadpool.ExecutorFactory;
import com.netty.client.core.threadpool.MessageSendTask;
import com.netty.client.innermsg.NettyMessage;
import com.netty.client.listener.EMMessageListener;
import com.netty.client.innermsg.Header;
import com.netty.client.innermsg.PayloadProto;
import com.netty.client.utils.MID;

import java.util.ArrayList;
import java.util.List;

import io.netty.channel.Channel;

/**
 * Created by robincxiao on 2017/9/1.
 */

public class EMMessageManager {
    private volatile static EMMessageManager sInstance;
    private List<EMMessageListener> mListeners;
    private Channel mChannel;

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

    public void setChannel(Channel channel) {
        this.mChannel = channel;
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
    public void sendPayload(String content) {
        PayloadProto.Payload payload = PayloadProto.Payload.newBuilder()
                .setMessageId(MID.getId()).setContent(content).build();

        NettyMessage message = new NettyMessage();
        message.msgType = Header.MsgType.PAYLOAD;
        message.body = payload;

        ExecutorFactory.submitSendTask(new MessageSendTask(mChannel, message));
    }

    /**
     * 获取已安装应用列表
     */
    public void requestAppList(){
        NettyMessage message = new NettyMessage();
        message.msgType = Header.MsgType.REQUEST;
        message.businessType = Header.BusinessType.REQUEST_APP_LIST;

        ExecutorFactory.submitSendTask(new MessageSendTask(mChannel, message));
    }

    /**
     * TV端更新
     */
    public void requestTvUpdate(){
        NettyMessage message = new NettyMessage();
        message.msgType = Header.MsgType.REQUEST;
        message.businessType = Header.BusinessType.REQUEST_TV_UPDATE;

        ExecutorFactory.submitSendTask(new MessageSendTask(mChannel, message));
    }
}
