package com.netty.client.handler;



import com.netty.client.common.ETvModelID;
import com.netty.client.innermsg.Header;
import com.netty.client.innermsg.NettyMessage;
import com.netty.client.utils.L;

import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;


/**
 * Created by robincxiao on 2017/10/17.
 * 处理接收消息统计
 */

public class StatisticsRecvHandler extends MessageToMessageDecoder<NettyMessage> {
    private StringBuilder builder = new StringBuilder();

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, NettyMessage message, List list) throws Exception {
        setStatistics(message);
        list.add(message);
    }

    private void setStatistics(NettyMessage message){
        //写日志
        if(message.msgType != Header.MsgType.PONG) {
            builder.append("recv::").append("msgType=").append(message.msgType).append(",businessType=").append(message.businessType);
            L.writeFile(builder.toString());
            builder.delete(0, builder.length());
        }

        if(message.msgType == Header.MsgType.RESPONSE){
            switch (message.businessType){
                case Header.BusinessType.RESPONSE_APP_ADDED:
                    //更新和安装使用相同的id=EMID_Secure_RemoteControl_TVcommunication_Recieve_Response_Install_Application
                    ETvModelID.saveActionData(ETvModelID.EMID_Secure_RemoteControl_TVcommunication_Recieve_Response_Install_Application);
                    break;
                case Header.BusinessType.RESPONSE_APP_REMOVED:
                    ETvModelID.saveActionData(ETvModelID.EMID_Secure_RemoteControl_TVcommunication_Recieve_Response_Delete_Application);
                    break;
                case Header.BusinessType.RESPONSE_APP_UPDATE:
                    ETvModelID.saveActionData(ETvModelID.EMID_Secure_RemoteControl_TVcommunication_Recieve_Response_Install_Application);
                    break;
                case Header.BusinessType.RESPONSE_APP_LIST:
                    ETvModelID.saveActionData(ETvModelID.EMID_Secure_RemoteControl_TVcommunication_Recieve_Response_Application_List);
                    break;
                case Header.BusinessType.RESPONSE_CLEAN:
                    ETvModelID.saveActionData(ETvModelID.EMID_Secure_RemoteControl_TVcommunication_Recieve_Response_finish_Cleanspeed);
                    break;
                case Header.BusinessType.RESPONSE_RESOURCE_RATE:
                    ETvModelID.saveActionData(ETvModelID.EMID_Secure_RemoteControl_TVcommunication_Recieve_Response_Occupation_Rate);
                    break;
                case Header.BusinessType.RESPONSE_DEVICE_INFO:
                    ETvModelID.saveActionData(ETvModelID.EMID_Secure_RemoteControl_TVcommunication_Recieve_Response_Deviceinfo);
                    break;
            }
        }
    }
}
