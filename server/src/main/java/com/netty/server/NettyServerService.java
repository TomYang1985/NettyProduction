package com.netty.server;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.netty.server.core.EMAcceptor;
import com.netty.server.listener.EMConnectionListener;
import com.netty.server.listener.EMMessageListener;
import com.netty.server.msg.EMMessage;
import com.netty.server.multicast.MulticastServer;
import com.netty.server.utils.L;

/**
 * Created by robincxiao on 2017/8/23.
 */

public class NettyServerService extends Service{
    private MulticastServer mMulticastServer;
    private WifiManager.MulticastLock mMulticastLock;

    @Override
    public void onCreate() {
        super.onCreate();

        WifiManager wifiManager=(WifiManager)this.getSystemService(Context.WIFI_SERVICE);
        mMulticastLock=wifiManager.createMulticastLock("multicast.server");
        mMulticastLock.acquire();

        EMAcceptor.getInstance().init(this);

        mMulticastServer = new MulticastServer();
        mMulticastServer.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        EMAcceptor.getInstance().start();
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
        EMAcceptor.getInstance().onDestory();
        mMulticastServer.stop();
        super.onDestroy();
    }
}
