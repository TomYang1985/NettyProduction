package com.netty.client.core.threadpool;

import com.netty.client.innermsg.NettyMessage;
import com.netty.client.innermsg.Header;
import com.netty.client.utils.L;

import io.netty.channel.Channel;

/**
 * Created by xiaoguochang on 2017/8/27.
 */

public class MessageSendTask implements Runnable {
    private Channel mChannel;
    private NettyMessage mMessage;

    public MessageSendTask(Channel channel, NettyMessage message) {
        mChannel = channel;
        mMessage = message;
    }


    @Override
    public void run() {
        if (mChannel == null) {
            L.print("MessageSendTask mChannel = null");
            return;
        }

        if (!mChannel.isActive()) {
            L.print("MessageSendTask channel != Active");
            return;
        }
        if (!mChannel.isWritable()) {
            L.print("MessageSendTask channel is not Writable");
            return;
        }

        switch (mMessage.msgType) {
            case Header.MsgType.PING:
                L.print("MessageSendTask send ping to" + mChannel.remoteAddress());
                mChannel.writeAndFlush(mMessage);
                break;
            case Header.MsgType.PAYLOAD:
                mChannel.writeAndFlush(mMessage);
                break;
            case Header.MsgType.EXCHANGE_KEY://发送key
                mChannel.writeAndFlush(mMessage);
                break;
            case Header.MsgType.REQUEST://发送request
                requestBusiness();
                break;
        }
    }

    /**
     * 业务请求的发送
     */
    private void requestBusiness(){
        switch (mMessage.businessType){
            case Header.BusinessType.REQUEST_APP_LIST://已安装APP列表
            case Header.BusinessType.REQUEST_TV_UPDATE://更新TV端
            case Header.BusinessType.REQUEST_CLEAN://清理
            case Header.BusinessType.REQUEST_OPEN_APP://打开APP
            case Header.BusinessType.REQUEST_REMOVE_APP://删除APP
            case Header.BusinessType.REQUEST_UPDATE_APP://更新APP
            case Header.BusinessType.REQUEST_DOWNLOAD_APP://下载APP
            case Header.BusinessType.REQUEST_OPEN_SETTING://打开设置
            case Header.BusinessType.REQUEST_RESOURCE_RATE://资源占用率
            case Header.BusinessType.REQUEST_DEVICE_INFO://请求设备信息
                mChannel.writeAndFlush(mMessage);
                break;
        }
    }
}
