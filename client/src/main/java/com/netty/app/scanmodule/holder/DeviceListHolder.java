package com.netty.app.scanmodule.holder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.netty.app.scanmodule.adapter.DeviceListAdapter;
import com.netty.client.R;
import com.netty.client.multicast.Device;

import butterknife.BindView;
import xiao.framework.viewholder.XGCRecyclerViewHolderExt;

/**
 * Created by robincxiao on 2017/8/30.
 */

public class DeviceListHolder extends XGCRecyclerViewHolderExt<Device, DeviceListAdapter>{
    @BindView(R.id.img_device_logo)
    ImageView mDeviceLogoImg;
    @BindView(R.id.text_name)
    TextView mDeviceNameText;
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
    public void setData(Device data) {
        mDeviceNameText.setText(data.name);
        mDeviceAddressText.setText(data.ip);
    }

    @Override
    protected void initWidgets() {

    }
}
