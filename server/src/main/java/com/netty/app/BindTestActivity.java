package com.netty.app;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.TextView;

import com.netty.app.common.template.EmptyTemplate;
import com.netty.server.R;
import com.tencent.tvmanager.util.L;

import java.util.concurrent.ThreadFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import xiao.framework.activity.BaseFragmentActivity;
import xiao.framework.template.BaseTemplate;

/**
 * Created by robincxiao on 2017/11/24.
 */

/**
 * 测试端口被占用的服务端
 */
public class BindTestActivity extends BaseFragmentActivity {
    private int port1 = 9987;
    private int port2 = 9988;
    private EventLoopGroup worker1;
    private EventLoopGroup worker2;

    @Override
    protected void doOnCreate(Bundle savedInstanceState) {

        TextView textView = (TextView) findViewById(R.id.text_ip);
        String ip = getLocalIpAddress();
        textView.setText(ip);

        bind1();
        bind2();
    }

    @Override
    protected BaseTemplate createTemplate() {
        return new EmptyTemplate(this);
    }

    @Override
    protected int getContentLayout() {
        return R.layout.actiivity_bind_test;
    }

    private void bind1() {
        ThreadFactory workerFactory = new DefaultThreadFactory("netty.server1");
        worker1 = new NioEventLoopGroup(1, workerFactory);
        ServerBootstrap bootstrap = new ServerBootstrap().group(worker1)
                .option(ChannelOption.SO_BACKLOG, 32768)
                .option(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.ALLOW_HALF_CLOSURE, false)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {

                    }
                });
        bootstrap.bind(port1)
                .addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (future.isSuccess()) {
                            L.print("test bind succ = " + port1);
                        } else {
                            L.print("test bind fail = " + port1);
                        }
                    }
                });
    }

    private void bind2() {
        ThreadFactory workerFactory = new DefaultThreadFactory("netty.server2");
        worker2 = new NioEventLoopGroup(1, workerFactory);
        ServerBootstrap bootstrap = new ServerBootstrap().group(worker2)
                .option(ChannelOption.SO_BACKLOG, 32768)
                .option(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.ALLOW_HALF_CLOSURE, false)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {

                    }
                });
        bootstrap.bind(port2)
                .addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (future.isSuccess()) {
                            L.print("test bind succ = " + port2);
                        } else {
                            L.print("test bind fail = " + port2);
                        }
                    }
                });
    }

    public String getLocalIpAddress() {
        //获取wifi服务
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //判断wifi是否开启
        if (wifiManager != null && wifiManager.isWifiEnabled()) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            return intToIp(ipAddress);
        }

        return "";
    }

    private String intToIp(int i) {
        return (i & 0xFF ) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ((i >> 16 ) & 0xFF) + "." +
                ( i >> 24 & 0xFF) ;
    }
}
