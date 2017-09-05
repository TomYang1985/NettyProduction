package com.netty.app.scanmodule.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.netty.app.scanmodule.entity.Device;
import com.netty.app.scanmodule.holder.DeviceListHolder;
import com.netty.client.R;
import com.netty.client.multicast.EMDevice;

import java.util.ArrayList;

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
        for (Device device : getDatas()) {
            if (device.id.equals(model.id)) {
                contain = true;
            }
        }

        if (!contain) {
            super.addLastItem(model);
        }
    }


    public void removeAll(ArrayList<Device> devices) {
        for (Device disconnectEMDevice : devices) {
            for (int i = 0; i < getDatas().size(); i++) {
                if(getDatas().get(i).id.equalsIgnoreCase(disconnectEMDevice.id)){
                    getDatas().remove(i);
                }
            }
        }

        notifyDataSetChanged();
    }

    /**
     * 设置一连接设备的状态
     * @param connectDevice
     */
    public void setConnectedDevice(Device connectDevice){
        /**
         * 因为建立TCP连接的速度可能比扫描速度快，所以如果设备列表中没有该设备时，则直接添加
         * 场景：网络断开后且设备也不存在与扫描缓存列表中，开启网络后就会出现以上bug
         */
        boolean flag = false;
        for (Device device : getDatas()) {
            if (device.id.equals(connectDevice.id)) {
                device.isConnected = true;
                flag = true;
            }else {
                device.isConnected = false;
            }
        }

        if(!flag){
            addLastItem(connectDevice);
        }

        notifyDataSetChanged();
    }

    public void setDisconnectedDevice(String id){
        for (Device device : getDatas()) {
            if (device.id.equals(id)) {
                device.isConnected = false;
                break;
            }
        }

        notifyDataSetChanged();
    }
}
