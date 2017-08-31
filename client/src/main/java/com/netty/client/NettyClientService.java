package com.netty.client;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.netty.client.core.DefaultClientConnector;
import com.netty.client.multicast.ScanDevice;

/**
 * Created by robincxiao on 2017/8/23.
 */

public class NettyClientService extends Service {
    private DefaultClientConnector mClientConnector;
    private WifiManager.MulticastLock mMulticastLock;

    @Override
    public void onCreate() {
        super.onCreate();

        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        mMulticastLock = wifiManager.createMulticastLock("multicast.test");
        mMulticastLock.acquire();

        mClientConnector = new DefaultClientConnector();
        //mClientConnector.init(this);

        ScanDevice.getInstance().init(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //mClientConnector.connect();
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
        mClientConnector.onDestory();
        ScanDevice.getInstance().onDestory();
        super.onDestroy();
    }
}
