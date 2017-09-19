package com.tencent.tvmanager.netty.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.tencent.tvmanager.netty.common.InnerMessageHelper;
import com.tencent.tvmanager.netty.core.threadpool.ExecutorFactory;
import com.tencent.tvmanager.netty.core.threadpool.MessageSendTask;
import com.tencent.tvmanager.netty.innermsg.NettyMessage;
import com.tencent.tvmanager.util.L;

/**
 * Created by xiaoguochang on 2017/9/17.
 */

public class AppActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        PackageManager manager = context.getPackageManager();
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
            String packageName = intent.getData().getSchemeSpecificPart();
            //提交APP安装task
            NettyMessage message = InnerMessageHelper.createAppAction(1, packageName);
            ExecutorFactory.submitSendTask(new MessageSendTask(true, message));
            L.d("安装成功" + packageName);
        }
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
            String packageName = intent.getData().getSchemeSpecificPart();
            //提交APP卸载task
            NettyMessage message = InnerMessageHelper.createAppAction(2, packageName);
            ExecutorFactory.submitSendTask(new MessageSendTask(true, message));
            L.d("卸载成功" + packageName);
        }
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
            String packageName = intent.getData().getSchemeSpecificPart();
            L.d("替换成功" + packageName);
        }
    }
}
