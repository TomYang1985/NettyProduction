package com.tencent.tvmanager.netty.core.threadpool;

import com.tencent.tvmanager.netty.common.InnerMessageHelper;
import com.tencent.tvmanager.netty.core.EMConnectManager;
import com.tencent.tvmanager.netty.innermsg.NettyMessage;
import com.tencent.tvmanager.netty.innermsg.Header;
import com.tencent.tvmanager.util.L;

import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.Channel;

/**
 * Created by xiaoguochang on 2017/8/27.
 */

public class MessageSendTask implements Runnable {
    private Channel mChannel;
    private NettyMessage mMessage;
    private boolean isMulticast = false;//是否是多播

    public MessageSendTask(Channel channel, NettyMessage message) {
        mChannel = channel;
        mMessage = message;
    }

    public MessageSendTask(boolean isMulticast, NettyMessage message) {
        this.isMulticast = isMulticast;
        mMessage = message;
    }

    @Override
    public void run() {
        if(isMulticast){//组播消息
            multicast();
            return;
        }

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

        switch (mMessage.msgType){
            case Header.MsgType.PING:
                L.print("MessageSendTask send pong to" + mChannel.remoteAddress());
                mChannel.writeAndFlush(InnerMessageHelper.createPong());
                break;
            case Header.MsgType.EXCHANGE_KEY:
                mChannel.writeAndFlush(InnerMessageHelper.createKeyResponseSucc());
                break;
            case Header.MsgType.PAYLOAD:
                mChannel.writeAndFlush(mMessage);
                break;
            case Header.MsgType.RESPONSE://发送业务响应
                doResponseBusiness(mChannel, mMessage);
                break;
        }
    }

    /**
     * 业务请求发送
     *
     * @param channel
     * @param message
     */
    private void doResponseBusiness(Channel channel, NettyMessage message) {
        switch (message.businessType) {
            case Header.BusinessType.RESPONSE_APP_LIST: {
                channel.writeAndFlush(mMessage);
            }
            break;
        }
    }

    /**
     * 发送组播消息
     */
    private void multicast(){
        ConcurrentHashMap<String, Channel> channelMap = EMConnectManager.getInstance().getChannelGroup();
        for (Channel channel : channelMap.values()){
            if (channel == null) {
                L.print("multicast MessageSendTask mChannel = null");
                return;
            }

            if (!channel.isActive()) {
                L.print("multicast MessageSendTask channel != Active");
                return;
            }
            if (!channel.isWritable()) {
                L.print("multicast MessageSendTask channel is not Writable");
                return;
            }

            if(mMessage.msgType == Header.MsgType.RESPONSE){
                switch (mMessage.businessType){
                    case Header.BusinessType.RESPONSE_APP_ADDED:
                    case Header.BusinessType.RESPONSE_APP_REMOVED:
                        channel.writeAndFlush(mMessage);
                        break;
                }
            }
        }
    }

}
