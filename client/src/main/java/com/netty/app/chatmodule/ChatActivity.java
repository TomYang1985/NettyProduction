package com.netty.app.chatmodule;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.netty.app.common.template.WhiteTitleTemplate;
import com.netty.client.R;
import com.netty.client.core.EMClient;
import com.netty.client.listener.EMConnectionListener;
import com.netty.client.listener.EMMessageListener;
import com.netty.client.msg.EMMessage;
import com.netty.client.msg.EMDevice;
import com.netty.client.msg.EMPayload;
import com.netty.client.utils.L;
import com.netty.client.utils.T;

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
        public void onConnectSuccByUser(String id) {

        }

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
                    ((Activity)mContext).finish();
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
                    if(message.msgType == EMMessage.MSG_TYPE_PAYLOAD) {
                        EMPayload payload = (EMPayload) message;
                        mContentBuilder.append(payload.from).append("\n").append(payload.content).append("\n");
                        mChatContentText.setText(mContentBuilder.toString());
                    }else if(message.msgType == EMMessage.MSG_TYPE_SERVER_VERSION){
                        L.d(message);
                    }
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
        if(mConnnectedDevice != null){
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

    @OnClick({R.id.btn_send})
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
        }
    }


}
