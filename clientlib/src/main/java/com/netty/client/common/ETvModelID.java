package com.netty.client.common;

import android.util.Log;

import com.anguanjia.framework.base.PiInstance;
import com.netty.client.Config;
import com.netty.client.utils.L;

import java.io.Serializable;

import meri.util.FeatureUtil;


/**
 * Created by robincxiao on 2017/10/24.
 */

public class ETvModelID implements Serializable {
    public static final int EMID_Secure_RemoteControl_TVcommunication_ConnectTV_Successed_Count = 383215;//手机端连接成功次数
    public static final int EMID_Secure_RemoteControl_TVcommunication_Send_Request_Open_Application = 383216;//发送打开软件指令
    public static final int EMID_Secure_RemoteControl_TVcommunication_Send_Request_Update_Tvmanager = 383217;//发送更新电视管家指令
    public static final int EMID_Secure_RemoteControl_TVcommunication_Send_Request_TVsetting = 383218;//发送打开电视设置请求
    public static final int EMID_Secure_RemoteControl_TVcommunication_Send_Request_Deviceinfo = 383219;//发送设备信息请求
    public static final int EMID_Secure_RemoteControl_TVcommunication_Recieve_Response_Deviceinfo = 383220;//接收设备信息响应
    public static final int EMID_Secure_RemoteControl_TVcommunication_Send_Request_Occupation_Rate = 383221;//发送TV资源占用率请求
    public static final int EMID_Secure_RemoteControl_TVcommunication_Recieve_Response_Occupation_Rate = 383222;//接收TV资源占用率响应
    public static final int EMID_Secure_RemoteControl_TVcommunication_Send_Request_Start_Cleanspeed = 383223;//发送清理加速指令
    public static final int EMID_Secure_RemoteControl_TVcommunication_Recieve_Response_finish_Cleanspeed = 383224;//接收清理加速完成响应
    public static final int EMID_Secure_RemoteControl_TVcommunication_Send_Request_Application_List = 383225;//发送软件列表请求
    public static final int EMID_Secure_RemoteControl_TVcommunication_Recieve_Response_Application_List = 383226;//接收软件列表响应
    public static final int EMID_Secure_RemoteControl_TVcommunication_Send_Request_Delete_Application = 383227;//发送卸载软件指令
    public static final int EMID_Secure_RemoteControl_TVcommunication_Recieve_Response_Delete_Application = 383228;//接收卸载软件响应
    public static final int EMID_Secure_RemoteControl_TVcommunication_Send_Request_Update_Application = 383229;//发送更新软件指令
    public static final int EMID_Secure_RemoteControl_TVcommunication_Send_Request_Install_CloudApp = 383230;//发送更新软件指令
    public static final int EMID_Secure_RemoteControl_TVcommunication_Send_Request_Install_LocalApp = 383231;//发送安装本地软件指令
    public static final int EMID_Secure_RemoteControl_TVcommunication_Recieve_Response_Install_Application = 383232;//接收安装软件响应
    public static final int EMID_Secure_RemoteControl_TVcommunication_HTTP_SERVER_Bind_Fail = 383233;//HttpServer绑定端口失败
    public static final int EMID_Secure_RemoteControl_TVcommunication_HTTP_SERVER_Address_Already_In_Use = 383234;//HttpServer绑定端口已被占用


    public static void saveActionData(int modelId){
        if(!Config.isDebug) {
            FeatureUtil.reportAction(PiInstance.getPluginContext(508), modelId, FeatureUtil.FeatureType.TYPE_ADD_UP);
        }
    }
}
