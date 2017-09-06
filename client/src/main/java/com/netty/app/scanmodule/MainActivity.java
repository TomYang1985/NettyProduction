package com.netty.app.scanmodule;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.netty.app.chatmodule.ChatActivity;
import com.netty.app.common.template.WhiteTitleTemplate;
import com.netty.app.scanmodule.adapter.DeviceListAdapter;
import com.netty.app.scanmodule.entity.Device;
import com.netty.app.widget.RecycleViewDivider;
import com.netty.app.widget.dialog.LoadingDialog;
import com.netty.client.R;
import com.netty.client.core.EMClient;
import com.netty.client.listener.EMConnectionListener;
import com.netty.client.multicast.EMDevice;
import com.netty.client.multicast.ScanDevice;
import com.netty.client.utils.L;

import java.util.ArrayList;

import butterknife.BindView;
import xiao.framework.activity.BaseFragmentActivity;
import xiao.framework.adapter.XGCOnRVItemClickListener;
import xiao.framework.template.BaseTemplate;

public class MainActivity extends BaseFragmentActivity implements XGCOnRVItemClickListener {
    private static final int MSG_FINDED_DEVICES = 1;
    private static final int MSG_FIND_ONE_DEVICE = 2;
    private static final int MSG_DISCONNECT_DEVICES = 3;
    private static final int MSG_WIFI_DISABLE = 4;
    private static final int MSG_WIFI_DISCONNECT = 5;
    private static final int MSG_WIFI_CONNECTED = 6;
    private static final int MSG_TIMEOUT = 7;
    private static final int MSG_CONNECTED = 8;
    private static final int MSG_DISCONNECT = 9;

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;
    @BindView(R.id.text_hint)
    TextView mHintText;
    private DeviceListAdapter mAdpter;
    private LinearLayoutManager mLinearLayoutManager;
    private WhiteTitleTemplate mTemplate;
    private LoadingDialog mConnectLoadingDialog;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (mAdpter == null || mHintText == null) {
                return true;
            }

            switch (msg.what) {
                case MSG_FINDED_DEVICES:
                    ArrayList<Device> findedEMDevices = msg.getData().getParcelableArrayList("devices");
                    mAdpter.setDatas(findedEMDevices);
                    mHintText.setVisibility(View.GONE);
                    break;
                case MSG_FIND_ONE_DEVICE: {
                    Device device = msg.getData().getParcelable("devices");
                    mAdpter.addLastItem(device);
                    mHintText.setVisibility(View.GONE);
                }
                break;
                case MSG_DISCONNECT_DEVICES:
                    ArrayList<Device> disconnectDevices = msg.getData().getParcelableArrayList("devices");
                    mAdpter.removeAll(disconnectDevices);

                    if (mAdpter.getDatas().size() == 0) {
                        if (mHintText.getVisibility() != View.VISIBLE) {
                            mHintText.setText("未发现可连接设备");
                            mHintText.setVisibility(View.VISIBLE);
                        }
                    } else {
                        mHintText.setVisibility(View.GONE);
                    }
                    break;
                case MSG_WIFI_DISABLE:
                    mAdpter.clear();
                    mHintText.setText("需要在WIFI下使用，请先连接WiFi");
                    mHintText.setVisibility(View.VISIBLE);
                    break;
                case MSG_WIFI_DISCONNECT:
                    mAdpter.clear();
                    mHintText.setText("WIFI未连接，请先连接WiFi");
                    mHintText.setVisibility(View.VISIBLE);
                    break;
                case MSG_WIFI_CONNECTED:
                    mHintText.setText("正在扫描...");
                    mHintText.setVisibility(View.VISIBLE);
                    break;
                case MSG_TIMEOUT:
                    mHintText.setText("扫描超时，未发现可连接设备...");
                    mHintText.setVisibility(View.VISIBLE);
                    break;
                case MSG_CONNECTED: {
                    mHintText.setVisibility(View.GONE);
                    String id = msg.getData().getString("id");
                    EMDevice connectedDevice = EMClient.getInstance().getDevice();
                    Device device = new Device(connectedDevice);
                    device.isConnected = true;
                    mAdpter.setConnectedDevice(device);
                }
                break;
                case MSG_DISCONNECT: {
                    String id = msg.getData().getString("id");
                    mAdpter.setDisconnectedDevice(id);
                }
                break;
            }
            return true;
        }
    });

    private EMConnectionListener mEMConnectionListener = new EMConnectionListener() {

        @Override
        public void onConnected(String id) {
            if (mHandler != null) {
                Message msg = mHandler.obtainMessage(MSG_CONNECTED);
                Bundle bundle = new Bundle();
                bundle.putString("id", id);
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }
            if (mConnectLoadingDialog != null) {
                mConnectLoadingDialog.dismiss();
            }
        }

        @Override
        public void onDisconnected(String id) {
            if (mConnectLoadingDialog != null) {
                mConnectLoadingDialog.dismiss();
            }
            if (mHandler != null) {
                Message msg = mHandler.obtainMessage(MSG_DISCONNECT);
                Bundle bundle = new Bundle();
                bundle.putString("id", id);
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }
        }
    };

    @Override
    protected void doOnCreate(Bundle savedInstanceState) {
        isSetStatusBar = true;
        mStatusBarColorId = R.color.colorPrimary;

        mAdpter = new DeviceListAdapter(mContext);
        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.HORIZONTAL, 1, getResources().getColor(R.color.c_divide)));
        mRecyclerView.setAdapter(mAdpter);
        mAdpter.setOnRVItemClickListener(this);

        if (EMClient.getInstance().isActive()) {
            EMDevice emDevice = EMClient.getInstance().getDevice();
            Device device = new Device(emDevice);
            device.isConnected = true;
            mAdpter.addLastItem(device);
        }

        EMClient.getInstance().getEMConnectManager().addListener(mEMConnectionListener);

        ScanDevice.getInstance().setScanListener(new ScanDevice.ScanListener() {
            @Override
            public void findedDevice(ArrayList<EMDevice> devices) {
                if (mHandler != null) {
                    ArrayList<Device> list = new ArrayList<>();
                    //扫描到的设备需要判断是否是当前连接的设备，因为UI上有连接状态标志
                    for (EMDevice emDevice : devices) {
                        Device device = new Device(emDevice);
                        EMDevice connnectedDevice = EMClient.getInstance().getDevice();
                        if (connnectedDevice != null && connnectedDevice.id.equals(emDevice.id)) {
                            device.isConnected = true;
                        } else {
                            device.isConnected = false;
                        }
                        list.add(device);
                    }

                    Message msg = mHandler.obtainMessage(MSG_FINDED_DEVICES);
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("devices", list);
                    msg.setData(bundle);

                    mHandler.sendMessage(msg);
                }
            }

            @Override
            public void findOneDevice(final EMDevice emDevice) {
                if (mHandler != null) {
                    //扫描到的设备需要判断是否是当前连接的设备，因为UI上有连接状态标志
                    Device device = new Device(emDevice);
                    EMDevice connnectedDevice = EMClient.getInstance().getDevice();
                    if (connnectedDevice != null && connnectedDevice.id.equals(emDevice.id)) {
                        device.isConnected = true;
                    } else {
                        device.isConnected = false;
                    }

                    Message msg = mHandler.obtainMessage(MSG_FIND_ONE_DEVICE);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("devices", device);
                    msg.setData(bundle);

                    mHandler.sendMessage(msg);
                }
            }

            @Override
            public void disconnectDevices(final ArrayList<EMDevice> devices) {
                if (mHandler != null) {
                    ArrayList<Device> list = new ArrayList<>();
                    for (EMDevice emDevice : devices) {
                        list.add(new Device(emDevice));
                    }

                    Message msg = mHandler.obtainMessage(MSG_DISCONNECT_DEVICES);
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("devices", list);
                    msg.setData(bundle);

                    mHandler.sendMessage(msg);
                }
            }

            @Override
            public void wifiConnected() {
                if (mHandler != null) {
                    mHandler.sendEmptyMessage(MSG_WIFI_CONNECTED);
                }
            }

            @Override
            public void wifiDisabled() {
                if (mHandler != null) {
                    mHandler.sendEmptyMessage(MSG_WIFI_DISABLE);
                }
            }

            @Override
            public void wifiDisconnect() {
                if (mHandler != null) {
                    mHandler.sendEmptyMessage(MSG_WIFI_DISCONNECT);
                }
            }

            @Override
            public void timeout() {
                if (mHandler != null) {
                    mHandler.sendEmptyMessage(MSG_TIMEOUT);
                }
            }
        });
    }

    @Override
    protected BaseTemplate createTemplate() {
        mTemplate = new WhiteTitleTemplate(this);
        mTemplate.setTitleText("设备列表");
        return mTemplate;
    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_scan_device;
    }

    @Override
    public void onRVItemClick(ViewGroup parent, View itemView, int position) {
        EMDevice connnectedDevice = EMClient.getInstance().getDevice();
        Device willConnectDevice = mAdpter.getDatas().get(position);

        //判断点击的是否是当前设备
        if (EMClient.getInstance().isActive() && connnectedDevice != null && connnectedDevice.id.equals(willConnectDevice.id)) {
            startActivity(new Intent(mContext, ChatActivity.class));
        } else {
            mConnectLoadingDialog = new LoadingDialog(mContext).setMsg("正在连接");
            mConnectLoadingDialog.show();
            EMClient.getInstance().connectDevice(new EMDevice(willConnectDevice.id, willConnectDevice.name));
        }
    }

    @Override
    protected void onDestroy() {
        EMClient.getInstance().getEMConnectManager().removeListener(mEMConnectionListener);
        ScanDevice.getInstance().setScanListener(null);
        super.onDestroy();
    }
}
