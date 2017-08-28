package com.netty.client;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

import com.netty.client.core.DefaultClientConnector;

/**
 * Created by robincxiao on 2017/8/23.
 */

public class NettyClientService extends Service{
    DefaultClientConnector mClientConnector;

    @Override
    public void onCreate() {
        super.onCreate();

        mClientConnector = new DefaultClientConnector();
        mClientConnector.init(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mClientConnector.connect();
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
        super.onDestroy();
    }
}
