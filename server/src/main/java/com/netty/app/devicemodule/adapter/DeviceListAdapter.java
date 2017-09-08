package com.netty.app.devicemodule.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.netty.app.devicemodule.holder.DeviceListHolder;
import com.netty.server.R;
import com.netty.server.msg.EMDevice;

import java.util.ArrayList;

import xiao.framework.adapter.XGCRecyclerViewAdapter;


/**
 * Created by robincxiao on 2017/8/30.
 */

public class DeviceListAdapter extends XGCRecyclerViewAdapter<EMDevice, DeviceListHolder> {
    public DeviceListAdapter(Context context) {
        super(context);
    }

    @Override
    protected DeviceListHolder createViewHolder(Context context, ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_device, parent, false);
        itemView.setFocusable(true);
        return new DeviceListHolder(context, this, parent, itemView, viewType);
    }

    @Override
    protected void setItemData(int position, DeviceListHolder holder, EMDevice model, int viewType) {
        holder.setData(model);
    }

    @Override
    public void addLastItem(EMDevice device) {
        boolean contain = false;
        for (EMDevice existDevice : getDatas()) {
            if (existDevice.id.equals(device.id)) {
                contain = true;
            }
        }

        if (!contain) {
            super.addLastItem(device);
        }
    }

    public void remove(EMDevice device){
        for (int i = 0; i < getDatas().size(); i++) {
            if (getDatas().get(i).id.equalsIgnoreCase(device.id)) {
                getDatas().remove(i);
            }
        }

        notifyDataSetChanged();
    }

    public void removeAll(ArrayList<EMDevice> devices) {
        for (EMDevice disconnectDevice : devices) {
            for (int i = 0; i < getDatas().size(); i++) {
                if (getDatas().get(i).id.equalsIgnoreCase(disconnectDevice.id)) {
                    getDatas().remove(i);
                }
            }
        }

        notifyDataSetChanged();
    }
}
