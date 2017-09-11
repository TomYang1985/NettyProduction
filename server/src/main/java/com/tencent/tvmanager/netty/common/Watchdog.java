package com.tencent.tvmanager.netty.common;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;

import com.tencent.tvmanager.util.L;

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;

/**
 * Created by robincxiao on 2017/8/21.
 */
public class Watchdog implements TimerTask {
    private static final int ALARM_TIMEOUT = 60000;
    private static final int MAX_RETRY_NUM = 4;
    private Context mContext;
    private int mCounter = 0;
    protected final HashedWheelTimer mTimer = new HashedWheelTimer();
    private WatchdogListener mWatchdogListener;
    private TimerReceiver mTimerReceiver;

    public Watchdog(Context context) {
        mContext = context;
        mTimerReceiver = new TimerReceiver();
        context.registerReceiver(mTimerReceiver, new IntentFilter(TimerReceiver.ACTION_TIMER));
        setAlarmRepeat();
    }

    public void setListener(WatchdogListener listener) {
        this.mWatchdogListener = listener;
    }

    @Override
    public void run(Timeout timeout) throws Exception {
        if (mWatchdogListener != null) {
            mWatchdogListener.disconnectRetry();
        }
    }

    public void setAlarmRepeat() {
        cancelAlarmRepeat();
        Intent intent = new Intent(TimerReceiver.ACTION_TIMER);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        long l = SystemClock.elapsedRealtime();
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, l, ALARM_TIMEOUT, pendingIntent);
    }

    private void cancelAlarmRepeat() {
        Intent intent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    public class TimerReceiver extends BroadcastReceiver {
        public final static String ACTION_TIMER = "netty.server.watchdog.timer";

        @Override
        public void onReceive(final Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_TIMER.equals(action) && mWatchdogListener != null) {
                mWatchdogListener.timerCheck();
            }
        }
    }

    public interface WatchdogListener {
        //连接断开重连
        void disconnectRetry();

        //定时检测
        void timerCheck();
    }

    public void onDestory() {
        L.print("Watchdog.onDestory");
        if (mContext != null && mTimerReceiver != null) {
            mContext.unregisterReceiver(mTimerReceiver);
        }
        cancelAlarmRepeat();
//        if (mTimer != null) {
//            mTimer.stop();
//        }
    }
}
