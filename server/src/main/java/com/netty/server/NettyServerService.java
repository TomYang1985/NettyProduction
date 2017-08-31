package com.netty.server;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.netty.server.core.DefaultServerAcceptor;
import com.netty.server.multicast.MulticastServer;

/**
 * Created by robincxiao on 2017/8/23.
 */

public class NettyServerService extends Service{
    public static DefaultServerAcceptor mServerAcceptor;
    private MulticastServer mMulticastServer;
    private WifiManager.MulticastLock mMulticastLock;

    @Override
    public void onCreate() {
        super.onCreate();

        WifiManager wifiManager=(WifiManager)this.getSystemService(Context.WIFI_SERVICE);
        mMulticastLock=wifiManager.createMulticastLock("multicast.test");
        mMulticastLock.acquire();

        mServerAcceptor = new DefaultServerAcceptor();
        mServerAcceptor.init(this);

//        mMulticastServer = new MulticastServer();
//        mMulticastServer.bind();

        mMulticastServer = new MulticastServer();
        mMulticastServer.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mServerAcceptor.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        mMulticastLock.release();
        mServerAcceptor.onDestory();
        mMulticastServer.stop();
        super.onDestroy();
    }
}
