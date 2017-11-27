package com.netty.client.handler;

import com.netty.client.codec.KeyManager;
import com.netty.client.common.Code;
import com.netty.client.core.threadpool.CallbackTask;
import com.netty.client.core.threadpool.ExecutorFactory;
import com.netty.client.core.threadpool.MessageRecvTask;
import com.netty.client.innermsg.CallbackMessage;
import com.netty.client.innermsg.Header;
import com.netty.client.innermsg.KeyResponseProto;
import com.netty.client.innermsg.NettyMessage;
import com.netty.client.utils.HostUtils;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by robincxiao on 2017/8/23.
 */
@ChannelHandler.Sharable
public class MessageRecvHandler extends SimpleChannelInboundHandler<NettyMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, NettyMessage message) throws Exception {
        switch (message.msgType) {
            case Header.MsgType.PAYLOAD:
                ExecutorFactory.submitRecvTask(new MessageRecvTask(channelHandlerContext.channel(), message));
                break;
            case Header.MsgType.EXCHANGE_KEY_RESP: {
                if (((KeyResponseProto.KeyResponse) message.body).getCode() == Code.RESULT_OK) {//密钥交换成功
                    KeyManager.getInstance().setKeyExchangeStatus(KeyManager.KEY_EXCHANGE_SUCC);

                    CallbackMessage callbackMessage = new CallbackMessage();
                    callbackMessage.type = CallbackMessage.MSG_TYPE_RECV_MSG;
                    callbackMessage.from = HostUtils.parseHost(channelHandlerContext.channel().remoteAddress().toString());
                    callbackMessage.recvMessage = message;
                    ExecutorFactory.submitCallbackTask(new CallbackTask(callbackMessage));
                } else {//密钥交换失败
                    KeyManager.getInstance().setKeyExchangeStatus(KeyManager.KEY_EXCHANGE_FAIL);
                    channelHandlerContext.channel().close();
                }
            }
            break;
            case Header.MsgType.RESPONSE:
                switch (message.businessType) {
                    case Header.BusinessType.RESPONSE_APP_ADDED://安装APP
                    case Header.BusinessType.RESPONSE_APP_REMOVED://删除APP
                    case Header.BusinessType.RESPONSE_APP_UPDATE://APP更新
                    case Header.BusinessType.RESPONSE_APP_LIST://已安装列表
                    case Header.BusinessType.RESPONSE_CLEAN://已安装列表
                    case Header.BusinessType.RESPONSE_RESOURCE_RATE://资源占用率
                    case Header.BusinessType.RESPONSE_DEVICE_INFO://资源占用率
                    case Header.BusinessType.RESPONSE_DOWNLOAD://下载
                        CallbackMessage callbackMessage = new CallbackMessage();
                        callbackMessage.type = CallbackMessage.MSG_TYPE_RECV_MSG;
                        callbackMessage.recvMessage = message;
                        ExecutorFactory.submitCallbackTask(new CallbackTask(callbackMessage));
                        break;
                }
                break;
        }
    }
}
