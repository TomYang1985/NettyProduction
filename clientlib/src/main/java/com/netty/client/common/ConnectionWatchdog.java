package com.netty.client.common;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Parcelable;
import android.os.SystemClock;

import com.netty.client.codec.KeyManager;
import com.netty.client.utils.L;
import com.netty.client.utils.NetUtils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;

/**
 * Created by robincxiao on 2017/8/21.
 */
@ChannelHandler.Sharable
public class ConnectionWatchdog extends ChannelInboundHandlerAdapter {
    private static final int ALARM_TIMEOUT = 60000;//周期定时(单位ms)
    private static final int KEY_EXCHANGE_TIMEOUT = 2;//密钥检测超时(单位s)
    private static final int MAX_RETRY_NUM = 3;
    private Context mContext;
    private volatile int mCounter = 0;//断线重连次数
    protected final HashedWheelTimer mTimer = new HashedWheelTimer();
    private WatchdogListener mWatchdogListener;
    private TimerReceiver mTimerReceiver;
    private NetChangeReceiver mNetChangeReceiver;
    /**
     * Watchdog是否使能
     * 1.Watchdog主要负责监控连接状态从已连接到断开连接后，进行重连的机制；
     * 2.当进行端口递增重连时，可能会产生channelInactive导致断线重连，这种情况下需要disable断线重连功能和定时重连功能，
     * 就是通过mWatchdogEnable来控制的
     */
    private AtomicBoolean mWatchdogEnable;
    private AtomicBoolean mWifiDisable;//WIFI是否正在关闭或已关闭
    private volatile boolean isServerLose = false;//检测不到服务端心跳

    public ConnectionWatchdog(Context context) {
        mContext = context;
        mWatchdogEnable = new AtomicBoolean(true);
        mWifiDisable = new AtomicBoolean(NetUtils.wifiIsDisable(context));
        mTimerReceiver = new TimerReceiver();
        mNetChangeReceiver = new NetChangeReceiver();
        context.registerReceiver(mTimerReceiver, new IntentFilter(TimerReceiver.ACTION_TIMER));
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        context.registerReceiver(mNetChangeReceiver, filter);

        setAlarmRepeat();
    }

    public void setListener(WatchdogListener listener) {
        this.mWatchdogListener = listener;
    }

    public void disableWatchdog() {
        mWatchdogEnable.set(false);
    }

    public void enableWatchdog() {
        mWatchdogEnable.set(true);
    }

    /**
     * 复位状态变量
     */
    public void reset() {
        mCounter = 0;
        isServerLose = false;
        mWatchdogEnable.set(true);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        L.print("ConnectionWatchdog.channelActive");
        //必须要复位的
        reset();

        checkKeyExchange();
        ctx.fireChannelActive();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                L.print("send ping");
                InnerMessageHelper.sendPing(ctx.channel());
            } else if (state == IdleState.READER_IDLE) {
                L.writeFile("server " + ctx.channel().remoteAddress() + " lose");
                isServerLose = true;
                ctx.channel().close();//server失联，关闭channel，一定要关闭连接，否则userEventTriggered会被不停调用
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    /**
     * 断线监测
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        L.print("ConnectionWatchdog.channelInactive");

        if (mWatchdogListener != null) {
            mWatchdogListener.channelInActive(ctx);
        }

        if(isServerLose){
            //如果是检测到服务端lose而自动断开的，则不进行断开重连机制
            InnerMessageHelper.sendDisconnectCallbackMessage();
        }else if (mWatchdogEnable.compareAndSet(true, true) && mTimer != null) {
            /**
             * wifi断开才进行断线重连，wifi关闭直接通知上层设备连接已断开
             * 但是，channelInactive回调的速度比监听WIFI状态的回调速度快，所以进行一个延时后，再对网络进行判断后决定
             * 进行断线重连还是通知上层设备连接已断开
             */
            mTimer.newTimeout(new DisconnectRetryTask(DisconnectRetryTask.TYPE_DISCONNECT_DELAY),
                    DisconnectRetryTask.DELAY, TimeUnit.MILLISECONDS);
        } else {
            L.print("channelInactive Watchdog disable");
        }

        ctx.fireChannelInactive();
    }

    /**
     * 断线重连
     */
    public void disconnectRetry() {
        if (mCounter == 0) {
            //mTimerEnable.set(false);//断线重连开始时，关闭定时重连
            //如果第一次尝试重连，需要通知上层，设备正在重连连接
            InnerMessageHelper.sendReconnectingCallbackMessage();
        } else if (mCounter >= MAX_RETRY_NUM) {
            //mTimerEnable.set(true);//断线重连未成功时，恢复定时重连
            //如果断线重连次数超过了MAX_RETRY_NUM次，需要通知上层，设备已断开连接
            InnerMessageHelper.sendDisconnectCallbackMessage();
        }

        if (mCounter++ < MAX_RETRY_NUM && mTimer != null) {
            //重连的间隔时间会越来越长
            int timeout = 2 << mCounter;
            mTimer.newTimeout(new DisconnectRetryTask(DisconnectRetryTask.TYPE_DISCONNECT_RETRY), timeout, TimeUnit.SECONDS);
        }
    }

    /**
     * 检测密钥交换是否成功
     * 一定时间定时器触发后，检测密钥交换是否成功
     */
    private void checkKeyExchange() {
        if (mTimer != null) {
            mTimer.newTimeout(new DisconnectRetryTask(DisconnectRetryTask.TYPE_KEY_EXCHANGE), KEY_EXCHANGE_TIMEOUT, TimeUnit.SECONDS);
        }
    }

    /**
     * 启动定时器
     */
    public void setAlarmRepeat() {
        cancelAlarmRepeat();
        Intent intent = new Intent(TimerReceiver.ACTION_TIMER);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        long l = SystemClock.elapsedRealtime();
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, l, ALARM_TIMEOUT, pendingIntent);
    }

    /**
     * 取消定时器
     */
    private void cancelAlarmRepeat() {
        Intent intent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    /**
     * 定时检测TCP连接状态（周期为ALARM_TIMEOUT）
     */
    public class TimerReceiver extends BroadcastReceiver {
        public final static String ACTION_TIMER = "netty.client.watchdog.timer";

        @Override
        public void onReceive(final Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_TIMER.equals(action)) {
                if (mWatchdogEnable.compareAndSet(true, true)) {
                    if (mWatchdogListener != null && !NetUtils.wifiIsDisable(mContext)) {
                        mWatchdogListener.timerCheck();
                    }
                } else {
                    L.writeFile("disable timer reconnect");
                }
            }
        }
    }

    /**
     * 断线重连task
     * 1.TCP连接成功后，发送定时任务检测密钥交换是否成功；
     * 2.TCP连接从连接到断开后，发送定时任务进行重试；
     */
    public class DisconnectRetryTask implements TimerTask {
        public static final int DELAY = 700;//ms
        private static final int TYPE_KEY_EXCHANGE = 1;//密钥交换
        private static final int TYPE_DISCONNECT_RETRY = 2;//断线重连
        private static final int TYPE_DISCONNECT_DELAY = 3;//进行断线重连功能时的延时
        private static final int TYPE_CONNECTED_DELAY = 4;//WIFI连接成功的延时
        private int type;

        public DisconnectRetryTask(int type) {
            this.type = type;
        }

        @Override
        public void run(Timeout timeout) throws Exception {
            if (mWatchdogListener != null) {
                switch (type) {
                    case TYPE_KEY_EXCHANGE:
                        int keyExchangeStatus = KeyManager.getInstance().getKeyExchangeStatus();
                        L.writeFile("keyExchangeStatus=" + keyExchangeStatus);
                        if (keyExchangeStatus == KeyManager.KEY_EXCHANGE_NULL) {
                            //服务端开发得到正确的校验(因为服务端没有返回校验包)
                            mWatchdogListener.unValidationServer();
                        }
                        break;
                    case TYPE_DISCONNECT_RETRY:
                        if (mWatchdogEnable.compareAndSet(true, true)) {
                            mWatchdogListener.disconnectRetry();
                        } else {
                            L.writeFile("disable disconnectRetry");
                        }
                        break;
                    case TYPE_DISCONNECT_DELAY:
                        if (mWifiDisable.get()) {
                            InnerMessageHelper.sendDisconnectCallbackMessage();
                        } else {
                            disconnectRetry();
                        }
                        break;
                    case TYPE_CONNECTED_DELAY:
                        L.writeFile("watchdog wifi connected");
                        if (mWatchdogEnable.compareAndSet(true, true)) {
                            if (mWatchdogListener != null) {
                                mWatchdogListener.wifiEnabled();
                            }
                        } else {
                            L.writeFile("disable wifi enabled connect");
                        }
                        break;
                }
            }
        }
    }

    /**
     * 监听Wifi的连接
     */
    public class NetChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }

            String action = intent.getAction();
            if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);
                switch (wifiState) {
                    case WifiManager.WIFI_STATE_DISABLED:
                        mWifiDisable.set(true);
                        break;
                    case WifiManager.WIFI_STATE_DISABLING:
                        mWifiDisable.set(true);
                        break;
                    case WifiManager.WIFI_STATE_ENABLING:
                        mWifiDisable.set(false);
                        break;
                    case WifiManager.WIFI_STATE_ENABLED:
                        mWifiDisable.set(false);
                        break;
                }
            } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
                Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (parcelableExtra != null) {
                    NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                    if (networkInfo != null) {
                        NetworkInfo.State state = networkInfo.getState();
                        if (state == NetworkInfo.State.CONNECTED) {
                            if (mTimer != null) {
                                mTimer.newTimeout(new DisconnectRetryTask(DisconnectRetryTask.TYPE_CONNECTED_DELAY),
                                        DisconnectRetryTask.DELAY, TimeUnit.MILLISECONDS);
                            }
                        }
                    }
                }
            }
        }
    }

    public interface WatchdogListener {
        //连接的监听回调，用于EMClient内部业务处理
        void channelInActive(ChannelHandlerContext ctx);

        //连接断开重连
        void disconnectRetry();

        //定时检测
        void timerCheck();

        //无法校验的服务端
        void unValidationServer();

        //wifi enabled
        void wifiEnabled();
    }

    public void onDestory() {
        L.print("ConnectionWatchdog.onDestory");
        if (mContext != null && mTimerReceiver != null) {
            mContext.unregisterReceiver(mTimerReceiver);
        }
        if (mContext != null && mNetChangeReceiver != null) {
            mContext.unregisterReceiver(mNetChangeReceiver);
        }
        cancelAlarmRepeat();
        if (mTimer != null) {
            mTimer.stop();
        }
    }
}
