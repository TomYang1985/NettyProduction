package com.example.client.sample.chatmodule;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.client.sample.R;
import com.example.client.sample.template.WhiteTitleTemplate;
import com.netty.client.core.EMClient;
import com.netty.client.listener.EMConnectionListener;
import com.netty.client.listener.EMMessageListener;
import com.netty.client.msg.EMDevice;
import com.netty.client.msg.EMMessage;
import com.netty.client.msg.EMPayload;
import com.netty.client.utils.GsonUtils;
import com.netty.client.utils.L;
import com.netty.client.utils.T;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import xiao.framework.activity.BaseFragmentActivity;
import xiao.framework.template.BaseTemplate;


/**
 * Created by robincxiao on 2017/9/1.
 */

public class ChatActivity extends BaseFragmentActivity implements View.OnClickListener {
    @BindView(R.id.text_chat_content)
    TextView mChatContentText;
    @BindView(R.id.edt_input)
    EditText mInputEdt;
    @BindView(R.id.btn_send)
    Button mSendBtn;
    private WhiteTitleTemplate mTemplate;
    private StringBuilder mContentBuilder;
    private EMDevice mConnnectedDevice;//当前连接的服务端设备
    private EMConnectionListener mEMConnectionListener = new EMConnectionListener() {

        @Override
        public void onChannelCheckSucc(String id) {

        }

        @Override
        public void onActive(String id) {

        }

        @Override
        public void onInActive(String id) {
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    T.showShort(mContext, mConnnectedDevice.id + "已断开");
                    ((Activity) mContext).finish();
                }
            });
        }

        @Override
        public void onError(int type) {

        }
    };

    private EMMessageListener mEMMessageListener = new EMMessageListener() {
        @Override
        public void onMessageReceived(final EMMessage message) {
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mChatContentText != null){
                        if(message.msgType != EMMessage.MSG_TYPE_APP_LIST){
                            mContentBuilder.append(GsonUtils.toJson(message)).append("\n---------------");
                            mChatContentText.setText(mContentBuilder.toString());
                        }
                        L.d(message);
                    }
//                    if (message.msgType == EMMessage.MSG_TYPE_PAYLOAD) {
//                        EMPayload payload = (EMPayload) message;
//                        mContentBuilder.append(payload.from).append("\n").append(payload.content).append("\n");
//                        mChatContentText.setText(mContentBuilder.toString());
//                    } else {
//                        if(message.msgType != EMMessage.MSG_TYPE_APP_LIST){
//                            mContentBuilder.append(GsonUtils.toJson(message));
//                        }
//                        L.d(message);
//                    }
                }
            });
        }
    };

    @Override
    protected void doOnCreate(Bundle savedInstanceState) {
        isSetStatusBar = true;
        mStatusBarColorId = R.color.colorPrimary;

        mContentBuilder = new StringBuilder();
        mConnnectedDevice = EMClient.getInstance().getDevice();
        EMClient.getInstance().getEMConnectManager().addListener(mEMConnectionListener);
        EMClient.getInstance().getEMMessageManager().addListener(mEMMessageListener);
        if (mConnnectedDevice != null) {
            mTemplate.setTitleText(mConnnectedDevice.name + "(" + mConnnectedDevice.id + ")");
        }
    }

    @Override
    protected void onDestroy() {
        EMClient.getInstance().getEMConnectManager().removeListener(mEMConnectionListener);
        EMClient.getInstance().getEMMessageManager().removeListener(mEMMessageListener);
        super.onDestroy();
    }

    @Override
    protected BaseTemplate createTemplate() {
        mTemplate = new WhiteTitleTemplate(this);
        mTemplate.setImageResource(R.mipmap.ic_back);
        return mTemplate;
    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_chat;
    }

    @OnClick({R.id.btn_send, R.id.btn_app_list, R.id.btn_update_tv, R.id.btn_clean, R.id.btn_start_app, R.id.btn_remove_APP
            , R.id.btn_open_setting, R.id.btn_resource_rate, R.id.btn_device_info, R.id.btn_download, R.id.btn_local_download})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                String content = mInputEdt.getText().toString();
                if (TextUtils.isEmpty(content)) {
                    return;
                }
                EMClient.getInstance().getEMMessageManager().sendPayload(content);
                mContentBuilder.append(EMClient.getInstance().localHost()).append("\n").append(content).append("\n");
                mChatContentText.setText(mContentBuilder.toString());
                mInputEdt.setText("");
                break;
            case R.id.btn_app_list:
                if(EMClient.getInstance().getEMMessageManager().requestAppList() == 0){
                    T.showShort(mContext, "发送成功");
                }else {
                    T.showShort(mContext, "发送失败");
                }
                L.d("host=" + EMClient.getInstance().remoteHost());
                break;
            case R.id.btn_update_tv:
                EMClient.getInstance().getEMMessageManager().requestTvUpdate();
                break;
            case R.id.btn_clean:
                EMClient.getInstance().getEMMessageManager().requestClean();
                break;
            case R.id.btn_start_app:
                EMClient.getInstance().getEMMessageManager().startApp("com.yunxun.wifipassword");
                break;
            case R.id.btn_remove_APP:
                EMClient.getInstance().getEMMessageManager().removeApp("com.yunxun.wifipassword");
                break;
            case R.id.btn_open_setting:
                EMClient.getInstance().getEMMessageManager().startSetting();
                break;
            case R.id.btn_resource_rate:
                EMClient.getInstance().getEMMessageManager().requestResourceRate();
                break;
            case R.id.btn_device_info:
                EMClient.getInstance().getEMMessageManager().requestDeviceInfo();
                break;
            case R.id.btn_download:
//                EMClient.getInstance().getEMMessageManager().updateApp("http://tvmgr.qq.com/dl/appstore/com.gitvdemo.video.apk", "云视听");
                //EMClient.getInstance().getEMMessageManager().downloadCloudApp("http://softfile.3g.qq.com/msoft/misc/QQDoctor.apk", "QQDoctor");
                EMClient.getInstance().getEMMessageManager().downloadCloudApp("http://tvmgr.qq.com/dl/appstore/com.gitvdemo.video.apk", "云视听");
                //EMClient.getInstance().getEMMessageManager().downloadCloudApp("http://tvmgr.qq.com/dl/appstore/com.ktcp.video.apk", "ktcp");
                break;
            case R.id.btn_local_download:
                String path = EMClient.getInstance().getLocalUrl("Download/沙发管家V5.0_v5.0.4_webmarket.apk");
                EMClient.getInstance().getEMMessageManager().downloadLocalApp(path, "沙发管家");
                break;
        }
    }
}
