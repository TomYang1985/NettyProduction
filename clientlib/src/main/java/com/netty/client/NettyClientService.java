package com.netty.client;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.IBinder;

import com.netty.client.core.EMClient;
import com.netty.client.multicast.ScanDevice;

/**
 * Created by robincxiao on 2017/8/23.
 */

public class NettyClientService extends Service {
    private EMClient mClientConnector;
    private WifiManager.MulticastLock mMulticastLock;
    private WifiManager.WifiLock mWifiLock;

    @Override
    public void onCreate() {
        super.onCreate();

        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        mMulticastLock = wifiManager.createMulticastLock("multicast.lock");
        mWifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, "WifiLocKManager");

        if(mMulticastLock != null) {
            mMulticastLock.acquire();
        }
        if(mWifiLock != null) {
            mWifiLock.acquire();
        }

        mClientConnector = EMClient.getInstance();
        mClientConnector.init(this);

        ScanDevice.getInstance().init(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ScanDevice.getInstance().start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if(mMulticastLock != null && mMulticastLock.isHeld()) {
            mMulticastLock.release();
        }
        if(mWifiLock != null && mWifiLock.isHeld()){
            mWifiLock.release();
        }
        mClientConnector.onDestory();
        ScanDevice.getInstance().onDestory();
        super.onDestroy();
    }
}
