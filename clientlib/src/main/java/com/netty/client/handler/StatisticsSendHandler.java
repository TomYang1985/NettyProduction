package com.netty.client.handler;


import com.netty.client.common.ETvModelID;
import com.netty.client.innermsg.AppActionRequestProto;
import com.netty.client.innermsg.Header;
import com.netty.client.innermsg.NettyMessage;

import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;


/**
 * Created by robincxiao on 2017/10/17.
 * 处理发送消息统计
 */

public class StatisticsSendHandler extends MessageToMessageEncoder<NettyMessage> {
    private StringBuilder builder = new StringBuilder();

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, NettyMessage message, List<Object> list) throws Exception {
        setStatistics(message);
        list.add(message);
    }

    private void setStatistics(NettyMessage message) {
        //写日志
        if(message.msgType != Header.MsgType.PING) {
            builder.append("send::").append("msgType=").append(message.msgType).append(",businessType=").append(message.businessType);
            builder.delete(0, builder.length() - 1);
        }

        if (message.msgType == Header.MsgType.REQUEST) {
            switch (message.businessType) {
                case Header.BusinessType.REQUEST_TV_UPDATE:
                    ETvModelID.saveActionData(ETvModelID.EMID_Secure_RemoteControl_TVcommunication_Send_Request_Update_Tvmanager);
                    break;
                case Header.BusinessType.REQUEST_APP_LIST:
                    ETvModelID.saveActionData(ETvModelID.EMID_Secure_RemoteControl_TVcommunication_Send_Request_Application_List);
                    break;
                case Header.BusinessType.REQUEST_CLEAN:
                    ETvModelID.saveActionData(ETvModelID.EMID_Secure_RemoteControl_TVcommunication_Send_Request_Start_Cleanspeed);
                    break;
                case Header.BusinessType.REQUEST_OPEN_APP:
                    ETvModelID.saveActionData(ETvModelID.EMID_Secure_RemoteControl_TVcommunication_Send_Request_Open_Application);
                    break;
                case Header.BusinessType.REQUEST_REMOVE_APP:
                    ETvModelID.saveActionData(ETvModelID.EMID_Secure_RemoteControl_TVcommunication_Send_Request_Delete_Application);
                    break;
                case Header.BusinessType.REQUEST_DOWNLOAD_APP: {
                    if(message.body instanceof AppActionRequestProto.AppActionRequest) {
                        AppActionRequestProto.AppActionRequest body = (AppActionRequestProto.AppActionRequest) message.body;
                        switch (body.getDownloadType()){
                            case Header.DownloadType.DOWNLOAD_TYPE_CLOUD:
                                ETvModelID.saveActionData(ETvModelID.EMID_Secure_RemoteControl_TVcommunication_Send_Request_Install_CloudApp);
                                break;
                            case Header.DownloadType.DOWNLOAD_TYPE_LOCAL:
                                ETvModelID.saveActionData(ETvModelID.EMID_Secure_RemoteControl_TVcommunication_Send_Request_Install_LocalApp);
                                break;
                            case Header.DownloadType.DOWNLOAD_TYPE_UPDATE:
                                ETvModelID.saveActionData(ETvModelID.EMID_Secure_RemoteControl_TVcommunication_Send_Request_Update_Application);
                                break;
                        }
                    }
                    break;
                }
                case Header.BusinessType.REQUEST_OPEN_SETTING:
                    ETvModelID.saveActionData(ETvModelID.EMID_Secure_RemoteControl_TVcommunication_Send_Request_TVsetting);
                    break;
                case Header.BusinessType.REQUEST_RESOURCE_RATE:
                    ETvModelID.saveActionData(ETvModelID.EMID_Secure_RemoteControl_TVcommunication_Send_Request_Occupation_Rate);
                    break;
                case Header.BusinessType.REQUEST_DEVICE_INFO:
                    ETvModelID.saveActionData(ETvModelID.EMID_Secure_RemoteControl_TVcommunication_Send_Request_Deviceinfo);
                    break;
            }
        }
    }
}
