package com.netty.client.multicast;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Parcelable;
import android.text.TextUtils;

import com.netty.client.utils.GsonUtils;
import com.netty.client.utils.L;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import xiao.framework.util.NetUtils;

/**
 * Created by robincxiao on 2017/8/29.
 */

public class ScanDevice implements Runnable {
    public static final String BROADCAST_IP = "239.255.255.200";
    public static final int BOADCAST_PORT = 30001;
    private static final int DATA_LEN = 3 * 1024;
    private static final int LOSE_DURATION = 20000;
    private static final int MONITOR_DURATION = 3000;//监控扫描周期
    private static final int STATUS_NONE = 1;
    private static final int STATUS_RUNNING = 2;
    //private static final int STATUS_STOPING = 3;
    private volatile static ScanDevice sInstance;
    //定义广播的IP地址
    private InetAddress broadcastAddress = null;
    //定义接收网络数据的字符数组
    private byte[] inBuff = new byte[DATA_LEN];
    //以指定字节数组创建准备接受的DatagramPacket对象
    private DatagramPacket inPacket = new DatagramPacket(inBuff, inBuff.length);
    private MulticastSocket socket = null;
    private ConcurrentHashMap<String, EMDevice> mDevicesMap;
    private ScanListener mScanListener;
    private AtomicInteger mStatus;
    private AtomicInteger mMonitorThreadStatus;
    private Context mContext;
    private NetChangeReceiver mNetChangeReceiver;
    private HashedWheelTimer mTimer;
    private String mLastWifiName;//上一次连接的Wifi名
    private TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run(Timeout timeout) throws Exception {
            if (mScanListener != null && mDevicesMap != null && mDevicesMap.size() == 0) {
                mScanListener.timeout();
            }
        }
    };

    private ScanDevice() {

    }

    public static ScanDevice getInstance() {
        if (sInstance == null) {
            synchronized (ScanDevice.class) {
                if (sInstance == null) {
                    sInstance = new ScanDevice();
                }
            }
        }

        return sInstance;
    }

    public void setScanListener(ScanListener listener) {
        this.mScanListener = listener;
        //目的是为了更快的返回设备别表
        if (mDevicesMap != null && mDevicesMap.size() > 0 && mScanListener != null) {
            ArrayList<EMDevice> EMDeviceList = new ArrayList<>();
            EMDeviceList.addAll(mDevicesMap.values());
            mScanListener.findedDevice(EMDeviceList);
        }
    }

    public void init(Context context) {
        mContext = context;
        mNetChangeReceiver = new NetChangeReceiver();
        mStatus = new AtomicInteger(STATUS_NONE);
        mMonitorThreadStatus = new AtomicInteger(STATUS_NONE);
        try {
            broadcastAddress = InetAddress.getByName(BROADCAST_IP);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        mDevicesMap = new ConcurrentHashMap<>();
    }

    public void start() {
        if (mContext != null && !NetUtils.isWifi(mContext)) {
            L.print("ScanDevice return when net is not wifi");
            return;
        }

        if (mStatus.compareAndSet(STATUS_RUNNING, STATUS_RUNNING)) {
            L.print("socket thread return when running or stoping");
            L.writeFile("socket thread return when running or stoping");
            return;
        }
        mStatus.getAndSet(STATUS_RUNNING);

        L.print("connecting.........");
        L.writeFile("connecting.........");
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        mContext.registerReceiver(mNetChangeReceiver, filter);
        mDevicesMap.clear();
        try {
            socket = new MulticastSocket(BOADCAST_PORT);
            //socket.setNetworkInterface(NetworkInterface.getByName("wlan0"));
            socket.joinGroup(broadcastAddress);
            L.print("create multicastsocket succ");
            L.writeFile("create multicastsocket succ");
            mTimer = new HashedWheelTimer();
            mTimer.newTimeout(mTimerTask, 30, TimeUnit.SECONDS);//扫描设备超时
            new Thread(this).start();
            startMonitorThread();
        } catch (IOException e) {
            e.printStackTrace();
            L.print("create multicastsocket IOException");
            L.writeFile("create multicastsocket IOException");
            mStatus.set(STATUS_NONE);
        }
    }

    private void startMonitorThread() {
        if (mMonitorThreadStatus.compareAndSet(STATUS_RUNNING, STATUS_RUNNING)) {
            L.print("MonitorThread return when running or stoping");
            L.writeFile("MonitorThread return when running or stoping");
            return;
        }

        mMonitorThreadStatus.getAndSet(STATUS_RUNNING);

        new MonitorThread().start();
    }

    private void closeSocket() {
        if (mStatus != null) {
            mStatus.getAndSet(STATUS_NONE);
        }
        if (socket != null) {
            socket.close();
        }
    }

    public void onDestory() {
        closeSocket();
        if (mMonitorThreadStatus != null) {
            mMonitorThreadStatus.getAndSet(STATUS_NONE);
        }
        if (mContext != null) {
            mContext.unregisterReceiver(mNetChangeReceiver);
        }
        if (mTimer != null) {
            mTimer.stop();
        }
        sInstance = null;
    }

    @Override
    public void run() {
        while (true) {
            try {
                //读取Socket中的数据
                socket.receive(inPacket);
            } catch (IOException e) {
                mStatus.getAndSet(STATUS_NONE);
                e.printStackTrace();
            }

            if (mStatus.compareAndSet(STATUS_NONE, STATUS_NONE)) {
                L.print("receive thread closeSocket");
                L.writeFile("receive thread closeSocket");
                return;
            }

            String recvData = new String(inBuff, 0, inPacket.getLength());
            String ip = inPacket.getAddress().toString();
            if (!TextUtils.isEmpty(ip)) {
                ip = ip.substring(1, ip.length());
            }

            MulticastMsg multicastMsg = GsonUtils.genarateBean(recvData, MulticastMsg.class);

            EMDevice EMDevice;
            if (mDevicesMap.containsKey(ip)) {
                EMDevice = mDevicesMap.get(ip);
                EMDevice.lastActiveTime = System.currentTimeMillis();
                mDevicesMap.put(ip, EMDevice);
                L.print("update EMDevice :: " + EMDevice.toString());
                L.writeFile("update EMDevice :: " + EMDevice.toString());
            } else {
                mTimer.stop();//发现设备后停止超时设置
                EMDevice = new EMDevice(ip, multicastMsg.deviceName, System.currentTimeMillis());
                mDevicesMap.put(ip, EMDevice);
                L.print("find EMDevice :: " + EMDevice.toString());
                L.writeFile("find EMDevice :: " + EMDevice.toString());
                if (mScanListener != null) {
                    mScanListener.findOneDevice(EMDevice);
                }
            }
        }
    }

    private class MonitorThread extends Thread {

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(MONITOR_DURATION);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (mMonitorThreadStatus.compareAndSet(STATUS_NONE, STATUS_NONE)) {
                    L.print("monitor thread close");
                    L.writeFile("monitor thread close");
                    return;
                }

                if (mDevicesMap == null) {
                    L.print("monitor thread mDevicesMap == null");
                    L.writeFile("monitor thread mDevicesMap == null");
                    return;
                }

                ArrayList<EMDevice> disconnectEMDeviceList = new ArrayList<>();

                Enumeration<String> keys = mDevicesMap.keys();
                while (keys.hasMoreElements()) {
                    String ip = keys.nextElement();
                    if (System.currentTimeMillis() - mDevicesMap.get(ip).lastActiveTime > LOSE_DURATION) {
                        L.print("disconnect device :: " + mDevicesMap.get(ip).toString());
                        disconnectEMDeviceList.add(mDevicesMap.get(ip));
                        mDevicesMap.remove(ip);
                    }
                }

                if (mScanListener != null && disconnectEMDeviceList.size() > 0) {
                    L.print("mScanListener.disconnectDevices");
                    L.writeFile("mScanListener.disconnectDevices");
                    mScanListener.disconnectDevices(disconnectEMDeviceList);
                }
            }
        }
    }

    private class NetChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);
                switch (wifiState) {
                    case WifiManager.WIFI_STATE_DISABLED:
                        L.print("Wifi disabled");
                        L.writeFile("Wifi disabled");
                        if (mScanListener != null) {
                            mScanListener.wifiDisabled();
                        }
                        break;
                    case WifiManager.WIFI_STATE_DISABLING:
                        L.print("Wifi disabling");
                        L.writeFile("Wifi disabling");
                        break;
                    case WifiManager.WIFI_STATE_ENABLED:
                        L.print("Wifi enabled");
                        L.writeFile("Wifi enabled");
                        break;
                    case WifiManager.WIFI_STATE_ENABLING:
                        L.print("Wifi enabling");
                        L.writeFile("Wifi enabling");
                        if (mScanListener != null) {
                            mScanListener.wifiConnected();
                        }
                        break;
                    case WifiManager.WIFI_STATE_UNKNOWN:
                        L.print("Wifi unknown");
                        L.writeFile("Wifi unknown");
                        break;
                }
            }

            if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
                Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (parcelableExtra != null) {
                    NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                    if (networkInfo != null) {
                        NetworkInfo.State state = networkInfo.getState();
                        if (state == NetworkInfo.State.DISCONNECTED) {
                            L.print("Wifi disconnected");
                            L.writeFile("Wifi disconnected");
                            closeSocket();//Wifi断开时时，关闭接收socket
                            if (mScanListener != null) {
                                mScanListener.wifiDisconnect();
                            }
                        } else if (state == NetworkInfo.State.CONNECTED) {
                            String wifiName = networkInfo.getExtraInfo();
                            L.print("Wifi connected = " + wifiName);
                            L.writeFile("Wifi connected = " + wifiName);
                            //如果两次连接的不同网络，则清空设备列表
                            if (!TextUtils.isEmpty(mLastWifiName) && !mLastWifiName.equals(wifiName)) {
                                L.writeFile("Wifi name change clear deviceMap");
                                mDevicesMap.clear();
                            }
                            mLastWifiName = wifiName;
                            //Wifi连接时，创建socket
                            start();
                            if (mScanListener != null) {
                                mScanListener.wifiConnected();
                                /**
                                 * 如果设备列表中存在设备，则快速返回
                                 * 场景：网络出现闪端后快速又建立连接，这时快速返回设备列表
                                 */
                                if (mDevicesMap.size() > 0) {
                                    ArrayList<EMDevice> EMDeviceList = new ArrayList<>();
                                    EMDeviceList.addAll(mDevicesMap.values());
                                    mScanListener.findedDevice(EMDeviceList);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public interface ScanListener {
        /**
         * 已发现的设备
         *
         * @param devices
         */
        void findedDevice(ArrayList<EMDevice> devices);

        /**
         * 发现一台新设备
         *
         * @param device
         */
        void findOneDevice(EMDevice device);

        /**
         * 失连的设备
         *
         * @param devices
         */
        void disconnectDevices(ArrayList<EMDevice> devices);

        /**
         * wifi已连接上
         */
        void wifiConnected();

        /**
         * wifi关闭
         */
        void wifiDisabled();

        /**
         * wifi断开
         */
        void wifiDisconnect();

        /**
         * 扫描超时(未扫描到设备)
         */
        void timeout();
    }
}
