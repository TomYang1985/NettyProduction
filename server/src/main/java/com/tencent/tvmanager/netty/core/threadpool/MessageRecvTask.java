package com.tencent.tvmanager.netty.core.threadpool;

import com.tencent.tvmanager.netty.business.BusinessHelper;
import com.tencent.tvmanager.netty.common.Code;
import com.tencent.tvmanager.netty.common.InnerMessageHelper;
import com.tencent.tvmanager.netty.core.EMMessageManager;
import com.tencent.tvmanager.netty.innermsg.AppActionRequestProto;
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
        L.d(recvmessage);

        switch (recvmessage.businessType) {
            case Header.BusinessType.REQUEST_APP_LIST: {//收到请求已安装的APP列表信息
                String localHost = HostUtils.parseHost(channel.localAddress().toString());
                ExecutorFactory.submitSendTask(new MessageSendTask(channel, InnerMessageHelper.createAppList(localHost)));
            }
            break;
            case Header.BusinessType.REQUEST_TV_UPDATE: {//客户端发现TV端版本过低，发送更新TV APP请求

            }
            break;
            case Header.BusinessType.REQUEST_CLEAN: {//清理
                String id = HostUtils.parseHostPort(channel.remoteAddress().toString());
                EMMessageManager.getInstance().sendCleanResponse(id, Code.RESULT_OK, 0, 100,
                        200, 300, 400);
//                CleanRubbish.getInstance().statScan(EMAcceptor.getInstance().getContext(), id);            }
                break;
            }
            case Header.BusinessType.REQUEST_OPEN_APP: {//打开APP
                AppActionRequestProto.AppActionRequest body = (AppActionRequestProto.AppActionRequest) recvmessage.body;
                BusinessHelper.startApp(body.getPackageName());
            }
            break;
            case Header.BusinessType.REQUEST_REMOVE_APP: {//删除APP
                AppActionRequestProto.AppActionRequest body = (AppActionRequestProto.AppActionRequest) recvmessage.body;
                BusinessHelper.removApp(body.getPackageName());
            }
            break;
            case Header.BusinessType.REQUEST_UPDATE_APP: {//更新APP
                AppActionRequestProto.AppActionRequest body = (AppActionRequestProto.AppActionRequest) recvmessage.body;
                BusinessHelper.installApp(body.getPackageName(), body.getUrl());
            }
            break;
            case Header.BusinessType.REQUEST_INSTALL_APP: {//安装APP
                AppActionRequestProto.AppActionRequest body = (AppActionRequestProto.AppActionRequest) recvmessage.body;
                BusinessHelper.installApp(body.getPackageName(), body.getUrl());
            }
            break;
            case Header.BusinessType.REQUEST_OPEN_SETTING: {//打开设置页面
                BusinessHelper.openSetting();
            }
            break;
            case Header.BusinessType.REQUEST_RESOURCE_RATE: {//请求资源占用率
                ExecutorFactory.submitSendTask(new MessageSendTask(channel, InnerMessageHelper.createResourceRate()));
            }
            break;
            case Header.BusinessType.REQUEST_DEVICE_INFO: {//设备信息
                ExecutorFactory.submitSendTask(new MessageSendTask(channel, InnerMessageHelper.createDeviceInfo()));
            }
            break;
        }
    }
}
