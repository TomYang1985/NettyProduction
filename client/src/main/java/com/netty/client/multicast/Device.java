package com.netty.client.multicast;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;

/**
 * Created by robincxiao on 2017/8/30.
 */

public class Device implements Parcelable{
    public String ip;
    public String name;
    public long lastActiveTime;

    public Device(String ip, String name, long lastActiveTime) {
        this.ip = ip;
        this.name = name;
        this.lastActiveTime = lastActiveTime;
    }

    protected Device(Parcel in) {
        ip = in.readString();
        name = in.readString();
        lastActiveTime = in.readLong();
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
    public String toString() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ip", ip);
            jsonObject.put("name", name);
            jsonObject.put("lastActiveTime", formatYYMMDDHHMMSS(lastActiveTime));
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "";
    }

    public String formatYYMMDDHHMMSS(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(time);
        return dateString;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ip);
        dest.writeString(name);
        dest.writeLong(lastActiveTime);
    }
}
