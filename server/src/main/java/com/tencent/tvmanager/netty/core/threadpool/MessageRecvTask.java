package com.tencent.tvmanager.netty.core.threadpool;

import com.tencent.tvmanager.netty.common.InnerMessageHelper;
import com.tencent.tvmanager.netty.innermsg.CallbackMessage;
import com.tencent.tvmanager.netty.innermsg.Header;
import com.tencent.tvmanager.netty.innermsg.NettyMessage;
import com.tencent.tvmanager.netty.util.HostUtils;
import com.tencent.tvmanager.util.L;

import io.netty.channel.Channel;

/**
 * Created by xiaoguochang on 2017/8/27.
 */

public class MessageRecvTask implements Runnable {
    private Channel mChannel;
    private NettyMessage mMessage;

    public MessageRecvTask(Channel channel, NettyMessage message) {
        mChannel = channel;
        mMessage = message;
    }

    @Override
    public void run() {
        if (mChannel != null) {
            switch (mMessage.msgType) {
                case Header.MsgType.PAYLOAD:
                    String remoteAddress = mChannel.remoteAddress().toString();
                    CallbackMessage message = new CallbackMessage();
                    message.type = CallbackMessage.MSG_TYPE_RECV_MSG;
                    message.id = HostUtils.parseHostPort(remoteAddress);
                    message.recvMsg = mMessage;
                    ExecutorFactory.submitCallbackTask(new CallbackTask(message));
                    break;
                case Header.MsgType.REQUEST:
                    doBusiness(mChannel, mMessage);
                    break;
            }
        }
    }

    /**
     * 业务请求分发处理
     *
     * @param channel
     * @param recvmessage
     */
    private void doBusiness(Channel channel, NettyMessage recvmessage) {
        switch (recvmessage.businessType) {
            case Header.BusinessType.REQUEST_APP_LIST: {//收到请求已安装的APP列表信息
                ExecutorFactory.submitSendTask(new MessageSendTask(channel, InnerMessageHelper.createAppList()));
            }
            break;
            case Header.BusinessType.REQUEST_TV_UPDATE: {//更新APP
                L.d(recvmessage);
            }
            break;
        }
    }


}
