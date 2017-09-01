package com.netty.app.chatmodule;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.netty.app.common.template.WhiteTitleTemplate;
import com.netty.app.scanmodule.MainActivity;
import com.netty.client.R;
import com.netty.client.core.EMClient;
import com.netty.client.listener.EMConnectionListener;
import com.netty.client.listener.EMMessageListener;
import com.netty.client.msg.EMMessage;
import com.netty.client.utils.L;

import butterknife.BindView;
import xiao.framework.activity.BaseFragmentActivity;
import xiao.framework.template.BaseTemplate;


/**
 * Created by robincxiao on 2017/9/1.
 */

public class ChatActivity extends BaseFragmentActivity implements View.OnClickListener{
    @BindView(R.id.text_chat_content)
    TextView mChatContentText;
    private WhiteTitleTemplate mTemplate;
    private String mHost;
    private StringBuilder mContentBuilder;
    private EMConnectionListener mEMConnectionListener = new EMConnectionListener() {
        @Override
        public void onConnected() {
            L.d("onConnected");
        }

        @Override
        public void onDisconnected() {
            L.d("onDisconnected");
        }
    };

    private EMMessageListener mEMMessageListener = new EMMessageListener() {
        @Override
        public void onMessageReceived(final EMMessage message) {
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mContentBuilder.append("\n").append(message.from).append("\n").append(message.content);
                    mChatContentText.setText(mContentBuilder.toString());
                }
            });
        }
    };

    @Override
    protected void doOnCreate(Bundle savedInstanceState) {
        mContentBuilder = new StringBuilder();
        EMClient.getInstance().addConnectListener(mEMConnectionListener);
        EMClient.getInstance().addMessageListener(mEMMessageListener);
    }

    @Override
    protected void onDestroy() {
        EMClient.getInstance().removeConnectListener(mEMConnectionListener);
        EMClient.getInstance().removeMessageListener(mEMMessageListener);
        super.onDestroy();
    }

    @Override
    protected BaseTemplate createTemplate() {
        mTemplate = new WhiteTitleTemplate(this);
        mTemplate.mLeftImg.setImageResource(R.mipmap.ic_launcher_round);
        mTemplate.mLeftImg.setVisibility(View.VISIBLE);
        mTemplate.mLeftImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChatActivity.this, MainActivity.class));
            }
        });
        return mTemplate;
    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_chat;
    }

    @Override
    public void onClick(View v) {

    }


}
