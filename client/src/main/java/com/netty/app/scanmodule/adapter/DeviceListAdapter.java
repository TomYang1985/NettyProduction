package com.netty.app.scanmodule.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.netty.app.scanmodule.holder.DeviceListHolder;
import com.netty.client.R;
import com.netty.client.multicast.Device;

import java.util.ArrayList;
import java.util.List;

import xiao.framework.adapter.XGCRecyclerViewAdapter;

/**
 * Created by robincxiao on 2017/8/30.
 */

public class DeviceListAdapter extends XGCRecyclerViewAdapter<Device, DeviceListHolder> {
    public DeviceListAdapter(Context context) {
        super(context);
    }

    @Override
    protected DeviceListHolder createViewHolder(Context context, ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_device_list, parent, false);
        return new DeviceListHolder(context, this, parent, itemView, viewType);
    }

    @Override
    protected void setItemData(int position, DeviceListHolder holder, Device model, int viewType) {
        holder.setData(model);
    }

    @Override
    public void addLastItem(Device model) {
        boolean contain = false;
        for (Device existDevice : getDatas()) {
            if (existDevice.ip.equals(model.ip)) {
                contain = true;
            }
        }

        if (!contain) {
            super.addLastItem(model);
        }
    }


    public void removeAll(ArrayList<Device> devices) {
        for (Device disconnectDevice : devices) {
            for (int i = 0; i < getDatas().size(); i++) {
                if(getDatas().get(i).ip.equalsIgnoreCase(disconnectDevice.ip)){
                    getDatas().remove(i);
                }
            }
        }

        notifyDataSetChanged();
    }
}
