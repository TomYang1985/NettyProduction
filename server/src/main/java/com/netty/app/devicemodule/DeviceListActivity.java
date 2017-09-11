package com.netty.app.devicemodule;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.netty.app.chatmodule.ChatActivity;
import com.netty.app.common.template.WhiteTitleTemplate;
import com.netty.app.devicemodule.adapter.DeviceListAdapter;
import com.netty.app.widget.RecycleViewDivider;
import com.netty.server.R;
import com.tencent.tvmanager.netty.core.EMAcceptor;
import com.tencent.tvmanager.netty.listener.EMConnectionListener;
import com.tencent.tvmanager.netty.msg.EMDevice;

import java.util.List;

import butterknife.BindView;
import xiao.framework.activity.BaseFragmentActivity;
import xiao.framework.adapter.XGCOnRVItemClickListener;
import xiao.framework.template.BaseTemplate;

public class DeviceListActivity extends BaseFragmentActivity implements XGCOnRVItemClickListener {
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    private DeviceListAdapter mAdpter;
    private LinearLayoutManager mLinearLayoutManager;
    private WhiteTitleTemplate mTemplate;
    private EMConnectionListener mEMConnectionListener = new EMConnectionListener() {
        @Override
        public void onConnected(final EMDevice device) {
            if(mContext == null || mAdpter == null){
                return;
            }

            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdpter.addLastItem(device);
                }
            });
        }

        @Override
        public void onDisconnected(final EMDevice device) {
            if(mContext == null || mAdpter == null){
                return;
            }

            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdpter.remove(device);
                }
            });
        }

        @Override
        public void onConnected(List<EMDevice> devices) {
            mAdpter.setDatas(devices);
        }
    };

    @Override
    protected void doOnCreate(Bundle savedInstanceState) {
        isSetStatusBar = true;
        mStatusBarColorId = R.color.colorPrimary;
        mAdpter = new DeviceListAdapter(mContext);
        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.HORIZONTAL, 1, getResources().getColor(R.color.c_divide)));
        mRecyclerView.setAdapter(mAdpter);
        mAdpter.setOnRVItemClickListener(this);

        EMAcceptor.getInstance().getEMConnectManager().addListener(mEMConnectionListener);
    }

    @Override
    protected BaseTemplate createTemplate() {
        mTemplate = new WhiteTitleTemplate(this);
        mTemplate.setTitleText("已连接设备");
        return mTemplate;
    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_device_list;
    }

    @Override
    public void onRVItemClick(ViewGroup parent, View itemView, int position) {
        if(position < mAdpter.getDatas().size()) {
            String id = mAdpter.getDatas().get(position).id;
            Intent intent = new Intent(mContext, ChatActivity.class);
            intent.putExtra("id", id);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        EMAcceptor.getInstance().getEMConnectManager().removeListener(mEMConnectionListener);
        super.onDestroy();
    }
}
