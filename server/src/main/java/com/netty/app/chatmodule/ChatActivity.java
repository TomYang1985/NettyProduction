package com.netty.app.chatmodule;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.netty.app.common.template.WhiteTitleTemplate;
import com.netty.server.R;
import com.tencent.tvmanager.netty.core.EMAcceptor;
import com.tencent.tvmanager.netty.listener.EMConnectionListener;
import com.tencent.tvmanager.netty.listener.EMMessageListener;
import com.tencent.tvmanager.netty.msg.EMDevice;
import com.tencent.tvmanager.netty.msg.EMMessage;
import com.tencent.tvmanager.util.L;
import com.tencent.tvmanager.util.T;

import java.util.List;

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
    private String id;
    private String mDeviceName;
    private StringBuilder mContentBuilder;
    private EMConnectionListener mEMConnectionListener = new EMConnectionListener() {

        @Override
        public void onConnected(final EMDevice device) {

        }

        @Override
        public void onDisconnected(final EMDevice device) {
            L.d(device);
            if (mContext == null) {
                return;
            }

            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (device.id.equals(id)) {
                        T.showShort(mContext, id + "已断开");
                        finish();
                    }
                }
            });
        }

        @Override
        public void onConnected(List<EMDevice> devices) {

        }
    };

    private EMMessageListener mEMMessageListener = new EMMessageListener() {
        @Override
        public void onMessageReceived(final EMMessage message) {
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mContentBuilder.append(message.from).append("\n").append(message.content).append("\n");
                    mChatContentText.setText(mContentBuilder.toString());
                }
            });
        }
    };

    @Override
    protected void doOnCreate(Bundle savedInstanceState) {
        isSetStatusBar = true;
        mStatusBarColorId = R.color.colorPrimary;

        id = getIntent().getStringExtra("id");
        mTemplate.setTitleText(id);

        mContentBuilder = new StringBuilder();
        mDeviceName = TextUtils.isEmpty(android.os.Build.MODEL) ? "智能电视" : android.os.Build.MODEL;
        EMAcceptor.getInstance().getEMConnectManager().addListener(mEMConnectionListener);
        EMAcceptor.getInstance().getEMMessageManager().addListener(mEMMessageListener);
    }

    @Override
    protected void onDestroy() {
        EMAcceptor.getInstance().getEMConnectManager().removeListener(mEMConnectionListener);
        EMAcceptor.getInstance().getEMMessageManager().removeListener(mEMMessageListener);
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
                EMAcceptor.getInstance().getEMMessageManager().sendPayload(id, content);
                mContentBuilder.append(mDeviceName).append("\n").append(content).append("\n");
                mChatContentText.setText(mContentBuilder.toString());
                mInputEdt.setText("");
                break;
        }
    }


}
