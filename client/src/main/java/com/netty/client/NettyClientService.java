package com.netty.client;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.netty.client.core.EMClient;
import com.netty.client.multicast.ScanDevice;

/**
 * Created by robincxiao on 2017/8/23.
 */

public class NettyClientService extends Service {
    private EMClient mClientConnector;
    private WifiManager.MulticastLock mMulticastLock;

    @Override
    public void onCreate() {
        super.onCreate();

        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        mMulticastLock = wifiManager.createMulticastLock("multicast.lock");
        if(mMulticastLock != null) {
            mMulticastLock.acquire();
        }

        mClientConnector = EMClient.getInstance();
        mClientConnector.init(this);

        ScanDevice.getInstance().init(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mClientConnector.connect();
        ScanDevice.getInstance().start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if(mMulticastLock != null) {
            mMulticastLock.release();
        }
        mClientConnector.onDestory();
        ScanDevice.getInstance().onDestory();
        super.onDestroy();
    }
}
