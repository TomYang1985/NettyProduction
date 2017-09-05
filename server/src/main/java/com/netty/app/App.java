package com.netty.app;

import android.app.Application;
import android.content.Intent;
import com.netty.server.NettyServerService;

/**
 * Created by robincxiao on 2017/8/30.
 */

public class App extends Application{
    @Override
    public void onCreate() {
        super.onCreate();

        startService(new Intent(this, NettyServerService.class));
    }
}
