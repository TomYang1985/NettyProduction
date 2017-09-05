package com.netty.app.scanmodule.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.netty.client.multicast.EMDevice;

/**
 * Created by robincxiao on 2017/9/5.
 */

public class Device implements Parcelable{
    public String id;//设备的host即ip地址
    public String name;
    public boolean isConnected = false;

    public Device(EMDevice emDevice) {
        this.id = emDevice.id;
        this.name = emDevice.name;
    }

    protected Device(Parcel in) {
        id = in.readString();
        name = in.readString();
        isConnected = in.readByte() != 0;
    }

    public static final Creator<Device> CREATOR = new Creator<Device>() {
        @Override
        public Device createFromParcel(Parcel in) {
            return new Device(in);
        }

        @Override
        public Device[] newArray(int size) {
            return new Device[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeByte((byte) (isConnected ? 1 : 0));
    }
}
