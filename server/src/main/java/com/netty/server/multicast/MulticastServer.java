package com.netty.server.multicast;


import android.text.TextUtils;

import com.netty.server.utils.GsonUtils;
import com.netty.server.utils.L;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;

/**
 * Created by robincxiao on 2017/8/29.
 */

public class MulticastServer implements Runnable {
    public static final String BROADCAST_IP = "239.255.255.200";
    public static final int BOADCAST_PORT = 30003;
    private static final int BOADCAST_DURATION = 15000;
    //定义广播的IP地址
    private InetAddress broadcastAddress = null;
    private MulticastSocket socket = null;
    private boolean isRun = true;
    private String mMulticastData;
    private int mMulticastDataLenght;

    public MulticastServer() {
        try {
            broadcastAddress = InetAddress.getByName(BROADCAST_IP);
            socket = new MulticastSocket(BOADCAST_PORT);
            socket.joinGroup(broadcastAddress);
            //socket.setNetworkInterface(NetworkInterface.getByName("wlan0"));
            MulticastMsg mMulticastMsg = new MulticastMsg();
            mMulticastMsg.deviceName = TextUtils.isEmpty(android.os.Build.MODEL) ? "智能电视" : android.os.Build.MODEL;
            mMulticastData = GsonUtils.toJson(mMulticastMsg);
            mMulticastDataLenght = mMulticastData.getBytes().length;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        new Thread(this).start();
    }

    public void stop() {
        isRun = false;
        if (socket != null) {
            socket.close();
        }
    }

    @Override
    public void run() {
        while (isRun) {
            try {
                try {
                    Thread.sleep(BOADCAST_DURATION);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                L.print("send data " + mMulticastData);
                DatagramPacket packet = new DatagramPacket(mMulticastData.getBytes(), mMulticastDataLenght, broadcastAddress, BOADCAST_PORT);
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
