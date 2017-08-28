package com.netty.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

import com.netty.server.core.DefaultServerAcceptor;

/**
 * Created by robincxiao on 2017/8/23.
 */

public class NettyServerService extends Service{
    public static DefaultServerAcceptor mServerAcceptor;

    @Override
    public void onCreate() {
        super.onCreate();

        mServerAcceptor = new DefaultServerAcceptor();
        mServerAcceptor.init(this);
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
        mServerAcceptor.onDestory();
        super.onDestroy();
    }
}
