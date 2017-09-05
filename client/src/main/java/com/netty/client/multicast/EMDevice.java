package com.netty.client.multicast;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

/**
 * Created by robincxiao on 2017/8/30.
 */

public class EMDevice implements Parcelable{
    public String id;//设备的host即ip地址
    public String name;
    public long lastActiveTime;

    public EMDevice(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public EMDevice(String id, String name, long lastActiveTime) {
        this.id = id;
        this.name = name;
        this.lastActiveTime = lastActiveTime;
    }

    protected EMDevice(Parcel in) {
        id = in.readString();
        name = in.readString();
        lastActiveTime = in.readLong();
    }

    public static final Creator<EMDevice> CREATOR = new Creator<EMDevice>() {
        @Override
        public EMDevice createFromParcel(Parcel in) {
            return new EMDevice(in);
        }

        @Override
        public EMDevice[] newArray(int size) {
            return new EMDevice[size];
        }
    };

    @Override
    public String toString() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", id);
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
        dest.writeString(id);
        dest.writeString(name);
        dest.writeLong(lastActiveTime);
    }
}
