package com.example.client.sample.applistmodule;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.client.sample.R;
import com.example.client.sample.applistmodule.adapter.AppListAdapter;
import com.example.client.sample.template.WhiteTitleTemplate;
import com.netty.client.core.EMClient;
import com.netty.client.listener.EMMessageListener;
import com.netty.client.msg.EMAppList;
import com.netty.client.msg.EMMessage;
import com.netty.client.utils.T;

import butterknife.BindView;
import xiao.framework.activity.BaseFragmentActivity;
import xiao.framework.template.BaseTemplate;

/**
 * Created by robincxiao on 2017/11/6.
 */

public class AppListActivity extends BaseFragmentActivity {
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    private WhiteTitleTemplate mTemplate;
    private GridLayoutManager mGridLayoutManager;
    private AppListAdapter mAdapter;

    private EMMessageListener mEMMessageListener = new EMMessageListener() {
        @Override
        public void onMessageReceived(final EMMessage message) {
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(message.msgType == EMMessage.MSG_TYPE_APP_LIST) {
                        mContext.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.setDatas(((EMAppList)message).getAppInfos());
                            }
                        });
                    }
                }
            });
        }
    };

    @Override
    protected void doOnCreate(Bundle savedInstanceState) {
        mGridLayoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mAdapter = new AppListAdapter(mContext);
        mRecyclerView.setAdapter(mAdapter);

        EMClient.getInstance().getEMMessageManager().addListener(mEMMessageListener);

        if(EMClient.getInstance().getEMMessageManager().requestAppList() == 0){
            T.showShort(mContext, "发送成功");
        }else {
            T.showShort(mContext, "发送失败");
        }
    }

    @Override
    protected BaseTemplate createTemplate() {
        mTemplate = new WhiteTitleTemplate(this);
        mTemplate.setImageResource(R.mipmap.ic_back);
        return mTemplate;
    }

    @Override
    protected int getContentLayout() {
        return R.layout.layout_app_list;
    }

    @Override
    protected void onDestroy() {
        EMClient.getInstance().getEMMessageManager().removeListener(mEMMessageListener);
        super.onDestroy();
    }
}
