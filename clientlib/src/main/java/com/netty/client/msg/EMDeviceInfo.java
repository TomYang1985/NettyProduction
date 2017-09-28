package com.netty.client.msg;

/**
 * Created by xiaoguochang on 2017/9/17.
 */

public class EMDeviceInfo extends EMMessage {
    public String deviceName;
    public String brand;
    public String model;
    public long totalSd;
    public long availableSd;
    public long totalMem;
    public String resolution;
    public String deviceDpi;

    public EMDeviceInfo(String deviceName, String brand, String model, long totalSd, long availableSd, long totalMem, String resolution, String deviceDpi) {
        msgType = MSG_TYPE_DEVICE_INFO;
        this.deviceName = deviceName;
        this.brand = brand;
        this.model = model;
        this.totalSd = totalSd;
        this.availableSd = availableSd;
        this.totalMem = totalMem;
        this.resolution = resolution;
        this.deviceDpi = deviceDpi;
    }
}
