package com.netty.app.devicemodule.holder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.netty.app.devicemodule.adapter.DeviceListAdapter;
import com.netty.server.R;
import com.netty.server.msg.EMDevice;

import butterknife.BindView;
import xiao.framework.viewholder.XGCRecyclerViewHolderExt;

/**
 * Created by robincxiao on 2017/8/30.
 */

public class DeviceListHolder extends XGCRecyclerViewHolderExt<EMDevice, DeviceListAdapter> {
    @BindView(R.id.img_device_logo)
    ImageView mDeviceLogoImg;
    @BindView(R.id.text_ip)
    TextView mDeviceAddressText;

    /**
     * 子类必须要实现
     *
     * @param context
     * @param adapter
     * @param parent
     * @param itemView
     * @param viewType
     */
    public DeviceListHolder(Context context, DeviceListAdapter adapter, ViewGroup parent, View itemView, int viewType) {
        super(context, adapter, parent, itemView, viewType);
    }

    @Override
    public void setData(EMDevice data) {
        mDeviceAddressText.setText(data.id);
    }

    @Override
    protected void initWidgets() {

    }
}
