package com.netty.client.core.threadpool;


import com.netty.client.common.Code;
import com.netty.client.core.EMConnectManager;
import com.netty.client.core.EMMessageManager;
import com.netty.client.innermsg.AppActionResponseProto;
import com.netty.client.innermsg.AppListResponseProto;
import com.netty.client.innermsg.CallbackMessage;
import com.netty.client.innermsg.CleanResponseProto;
import com.netty.client.innermsg.DeviceInfoResponseProto;
import com.netty.client.innermsg.DownloadResponseProto;
import com.netty.client.innermsg.Header;
import com.netty.client.innermsg.KeyResponseProto;
import com.netty.client.innermsg.PayloadProto;
import com.netty.client.innermsg.ResourceRateResponseProto;
import com.netty.client.listener.EMConnectionListener;
import com.netty.client.listener.EMMessageListener;
import com.netty.client.msg.EMAppInstall;
import com.netty.client.msg.EMAppList;
import com.netty.client.msg.EMAppRemove;
import com.netty.client.msg.EMAppUpdate;
import com.netty.client.msg.EMDeviceInfo;
import com.netty.client.msg.EMDownload;
import com.netty.client.msg.EMMessage;
import com.netty.client.msg.EMPayload;
import com.netty.client.msg.EMResourceRate;
import com.netty.client.msg.EMRubbish;
import com.netty.client.msg.EMUpdate;

/**
 * Created by xiaoguochang on 2017/8/27.
 */

public class CallbackTask implements Runnable {
    private CallbackMessage message;

    public CallbackTask(CallbackMessage message) {
        this.message = message;
    }


    @Override
    public void run() {
        if (message == null) {
            return;
        }

        if (message.type == CallbackMessage.MSG_TYPE_ACTIVE) {
            for (EMConnectionListener listener : EMConnectManager.getInstance().getListener()) {
                if (listener != null) {
                    listener.onActive(message.from);
                }
            }
        } else if (message.type == CallbackMessage.MSG_TYPE_INACTIVE) {
            for (EMConnectionListener listener : EMConnectManager.getInstance().getListener()) {
                if (listener != null) {
                    listener.onInActive(message.from);
                }
            }
        } else if (message.type == CallbackMessage.MSG_TYPE_NOT_WIFI) {
            connectError(Code.CODE_NOT_WIFI);
        } else if (message.type == CallbackMessage.MSG_TYPE_CONNECTING) {
            connectError(Code.CODE_CONNECTING);
        } else if (message.type == CallbackMessage.MSG_TYPE_CONNECTED) {
            connectError(Code.CODE_CONNECTED);
        } else if (message.type == CallbackMessage.MSG_TYPE_HOST_NULL) {
            connectError(Code.CODE_HOST_NULL);
        } else if (message.type == CallbackMessage.MSG_TYPE_CONNECT_FAIL) {
            connectError(Code.CODE_CONNECT_FAIL);
        } else if (message.type == CallbackMessage.MSG_TYPE_CONNECT_SUCC_BY_USER) {
//            for (EMConnectionListener listener : EMConnectManager.getInstance().getListener()) {
//                if (listener != null) {
//                    listener.onConnectSuccByUser(message.from);
//                }
//            }
        } else if (message.type == CallbackMessage.MSG_TYPE_RECV_MSG) {
            switch (message.recvMessage.msgType) {
                case Header.MsgType.PAYLOAD: {
                    PayloadProto.Payload chatMsg = (PayloadProto.Payload) message.recvMessage.body;
                    EMPayload payload = new EMPayload(message.from, chatMsg.getContent());
                    callbackMessage(payload);
                }
                break;
                case Header.MsgType.EXCHANGE_KEY_RESP: {
                    //tcp channel密钥交换成功回调
                    for (EMConnectionListener listener : EMConnectManager.getInstance().getListener()) {
                        if (listener != null) {
                            listener.onChannelCheckSucc(message.from);
                        }
                    }
                    KeyResponseProto.KeyResponse body = (KeyResponseProto.KeyResponse) message.recvMessage.body;
                    int updateType = 0;
                    int protocol = body.getProtocol();
                    if (protocol > Header.PROTOCOL_VERSION) {
                        updateType = EMUpdate.UPDATE_TYPE_PHONE;
                    }
                    if (protocol < Header.PROTOCOL_VERSION) {
                        updateType = EMUpdate.UPDATE_TYPE_TV;
                    }
                    //更新提示回调
                    callbackMessage(new EMUpdate(updateType));
                }
                break;
                case Header.MsgType.RESPONSE:
                    doResponse();
                    break;
            }
        }
    }

    /**
     * 业务消息的分类处理
     */
    private void doResponse() {
        switch (message.recvMessage.businessType) {
            case Header.BusinessType.RESPONSE_APP_ADDED: {
                AppActionResponseProto.AppActionResponse body = (AppActionResponseProto.AppActionResponse) message.recvMessage.body;
                callbackMessage(new EMAppInstall(body.getPackageName(), body.getAppName(), body.getVersionCode()
                        , body.getVersionName(), body.getIsSystem(), body.getIconUrl()));

            }
            break;
            case Header.BusinessType.RESPONSE_APP_REMOVED: {
                AppActionResponseProto.AppActionResponse body = (AppActionResponseProto.AppActionResponse) message.recvMessage.body;
                callbackMessage(new EMAppRemove(body.getPackageName()));
            }
            break;
            case Header.BusinessType.RESPONSE_APP_UPDATE: {
                AppActionResponseProto.AppActionResponse body = (AppActionResponseProto.AppActionResponse) message.recvMessage.body;
                callbackMessage(new EMAppUpdate(body.getPackageName(), body.getAppName(), body.getVersionCode()
                        , body.getVersionName(), body.getIsSystem(), body.getIconUrl()));

            }
            break;
            case Header.BusinessType.RESPONSE_APP_LIST: {
                AppListResponseProto.AppListResponse body = (AppListResponseProto.AppListResponse) message.recvMessage.body;
                EMAppList installedApps = new EMAppList();
                for (AppListResponseProto.AppInfo appInfo : body.getListList()) {
                    installedApps.add(appInfo.getPackageName(), appInfo.getAppName(), appInfo.getVersionCode()
                            , appInfo.getVersionName(), appInfo.getIsSystem(), appInfo.getIconUrl());
                }
                callbackMessage(installedApps);
            }
            break;
            case Header.BusinessType.RESPONSE_CLEAN: {
                CleanResponseProto.CleanResponse body = (CleanResponseProto.CleanResponse) message.recvMessage.body;
                callbackMessage(new EMRubbish(body.getCode(), body.getSdkCode(), body.getMemRubbish(), body.getSysRubbish(), body.getCacheRubbish()
                        , body.getApkRubbish()));
            }
            break;
            case Header.BusinessType.RESPONSE_RESOURCE_RATE: {
                ResourceRateResponseProto.ResourceRateResponse body = (ResourceRateResponseProto.ResourceRateResponse) message.recvMessage.body;
                callbackMessage(new EMResourceRate(body.getResourceRate()));
            }
            break;
            case Header.BusinessType.RESPONSE_DEVICE_INFO: {
                DeviceInfoResponseProto.DeviceInfoResponse body = (DeviceInfoResponseProto.DeviceInfoResponse) message.recvMessage.body;
                callbackMessage(new EMDeviceInfo(body.getDeviceName(), body.getBrand(), body.getModel(), body.getTotalSd()
                        , body.getAvailableSd(), body.getTotalMem(), body.getResolution(), body.getDeviceDpi()));
            }
            break;
            case Header.BusinessType.RESPONSE_DOWNLOAD: {
                DownloadResponseProto.DownloadResponse body = (DownloadResponseProto.DownloadResponse) message.recvMessage.body;
                callbackMessage(new EMDownload(body.getCode(), body.getUrl()));
            }
            break;
        }
    }

    /**
     * 连接错误回调
     *
     * @param code
     */
    private void connectError(int code) {
        for (EMConnectionListener listener : EMConnectManager.getInstance().getListener()) {
            if (listener != null) {
                listener.onError(code);
            }
        }
    }

    private void callbackMessage(EMMessage message) {
        for (EMMessageListener listener : EMMessageManager.getInstance().getListener()) {
            if (listener != null) {
                listener.onMessageReceived(message);
            }
        }
    }
}
