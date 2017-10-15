package com.netty.client.core;

import com.netty.client.core.threadpool.ExecutorFactory;
import com.netty.client.core.threadpool.MessageSendTask;
import com.netty.client.innermsg.AppActionRequestProto;
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
    public int sendPayload(String content) {
        if(!isActive()){
            return -1;
        }

        PayloadProto.Payload payload = PayloadProto.Payload.newBuilder()
                .setMessageId(MID.getId()).setContent(content).build();

        NettyMessage message = new NettyMessage();
        message.msgType = Header.MsgType.PAYLOAD;
        message.body = payload;

        ExecutorFactory.submitSendTask(new MessageSendTask(mChannel, message));

        return 0;
    }

    private boolean isActive(){
        return EMClient.getInstance().isActive();
    }

    /**
     * 获取已安装应用列表
     */
    public int requestAppList(){
        if(!isActive()){
            return -1;
        }

        NettyMessage message = new NettyMessage();
        message.msgType = Header.MsgType.REQUEST;
        message.businessType = Header.BusinessType.REQUEST_APP_LIST;

        ExecutorFactory.submitSendTask(new MessageSendTask(mChannel, message));

        return 0;
    }

    /**
     * TV端更新
     */
    public int requestTvUpdate(){
        if(!isActive()){
            return -1;
        }

        NettyMessage message = new NettyMessage();
        message.msgType = Header.MsgType.REQUEST;
        message.businessType = Header.BusinessType.REQUEST_TV_UPDATE;

        ExecutorFactory.submitSendTask(new MessageSendTask(mChannel, message));

        return 0;
    }

    /**
     * 垃圾清理
     */
    public int requestClean(){
        if(!isActive()){
            return -1;
        }

        NettyMessage message = new NettyMessage();
        message.msgType = Header.MsgType.REQUEST;
        message.businessType = Header.BusinessType.REQUEST_CLEAN;

        ExecutorFactory.submitSendTask(new MessageSendTask(mChannel, message));

        return 0;
    }

    /**
     * 打开APP
     * @param packageName
     */
    public int startApp(String packageName){
        if(!isActive()){
            return -1;
        }

        AppActionRequestProto.AppActionRequest  body = AppActionRequestProto.AppActionRequest.newBuilder()
                .setMessageId(MID.getId())
                .setPackageName(packageName).build();
        NettyMessage message = new NettyMessage();
        message.msgType = Header.MsgType.REQUEST;
        message.businessType = Header.BusinessType.REQUEST_OPEN_APP;
        message.body = body;

        ExecutorFactory.submitSendTask(new MessageSendTask(mChannel, message));

        return 0;
    }

    /**
     * 删除APP
     * @param packageName
     */
    public int removeApp(String packageName){
        if(!isActive()){
            return -1;
        }

        AppActionRequestProto.AppActionRequest  body = AppActionRequestProto.AppActionRequest.newBuilder()
                .setMessageId(MID.getId())
                .setPackageName(packageName).build();
        NettyMessage message = new NettyMessage();
        message.msgType = Header.MsgType.REQUEST;
        message.businessType = Header.BusinessType.REQUEST_REMOVE_APP;
        message.body = body;

        ExecutorFactory.submitSendTask(new MessageSendTask(mChannel, message));

        return 0;
    }

    /**
     * 下载安装APP
     * @param url
     * @param appName
     */
    public int downloadApp(String url, String appName){
        if(!isActive()){
            return -1;
        }

        AppActionRequestProto.AppActionRequest  body = AppActionRequestProto.AppActionRequest.newBuilder()
                .setMessageId(MID.getId())
                .setUrl(url).setAppName(appName).build();
        NettyMessage message = new NettyMessage();
        message.msgType = Header.MsgType.REQUEST;
        message.businessType = Header.BusinessType.REQUEST_DOWNLOAD_APP;
        message.body = body;

        ExecutorFactory.submitSendTask(new MessageSendTask(mChannel, message));

        return 0;
    }

    /**
     * 启动系统setting页面
     */
    public int startSetting(){
        if(!isActive()){
            return -1;
        }

        NettyMessage message = new NettyMessage();
        message.msgType = Header.MsgType.REQUEST;
        message.businessType = Header.BusinessType.REQUEST_OPEN_SETTING;

        ExecutorFactory.submitSendTask(new MessageSendTask(mChannel, message));

        return 0;
    }

    /**
     * 请求资源占用率
     */
    public int requestResourceRate(){
        if(!isActive()){
            return -1;
        }

        NettyMessage message = new NettyMessage();
        message.msgType = Header.MsgType.REQUEST;
        message.businessType = Header.BusinessType.REQUEST_RESOURCE_RATE;

        ExecutorFactory.submitSendTask(new MessageSendTask(mChannel, message));

        return 0;
    }

    /**
     * 请求设备信息
     */
    public int requestDeviceInfo(){
        if(!isActive()){
            return -1;
        }

        NettyMessage message = new NettyMessage();
        message.msgType = Header.MsgType.REQUEST;
        message.businessType = Header.BusinessType.REQUEST_DEVICE_INFO;

        ExecutorFactory.submitSendTask(new MessageSendTask(mChannel, message));

        return 0;
    }
}
