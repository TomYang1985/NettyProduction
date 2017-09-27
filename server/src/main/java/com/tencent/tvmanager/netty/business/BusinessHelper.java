package com.tencent.tvmanager.netty.business;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.provider.Settings;

import com.tencent.tvmanager.netty.common.Code;
import com.tencent.tvmanager.netty.core.EMAcceptor;
import com.tencent.tvmanager.netty.httpserver.HttpServer;
import com.tencent.tvmanager.netty.innermsg.AppActionResponseProto;
import com.tencent.tvmanager.netty.innermsg.AppListResponseProto;
import com.tencent.tvmanager.netty.innermsg.ResourceRateResponseProto;
import com.tencent.tvmanager.netty.util.MID;

import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by robincxiao on 2017/9/18.
 */

/**
 * 业务逻辑帮助类
 */
public class BusinessHelper {
    /**
     * 获取已安装应用列表
     *
     * @return
     */
    public static AppListResponseProto.AppListResponse getPackages(String localHost) {
        // 获取已经安装的所有应用, PackageInfo　系统类，包含应用信息
        Context context = EMAcceptor.getInstance().getContext();
        PackageManager pm = context.getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);//Intent.CATEGORY_LEANBACK_LAUNCHER???
        //通过查询，获得所有ResolveInfo对象
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(mainIntent, 0);//PackageManager.MATCH_DEFAULT_ONLY X

        AppListResponseProto.AppListResponse.Builder builder = AppListResponseProto.AppListResponse.newBuilder()
                .setMessageId(MID.getId()).setCode(Code.RESULT_OK);

        for (ResolveInfo resolveInfo : resolveInfos) {
            String pkgName = resolveInfo.activityInfo.packageName;
            PackageInfo packageInfo = null;
            try {
                packageInfo = pm.getPackageInfo(pkgName, 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            if (packageInfo != null) {
                AppListResponseProto.AppInfo.Builder appInfoBuilder = AppListResponseProto.AppInfo.newBuilder();

                if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) { //非系统应用
                    appInfoBuilder.setIsSystem(false);
                } else { // 系统应用
                    appInfoBuilder.setIsSystem(true);
                }

                //添加packageName
                appInfoBuilder.setPackageName(packageInfo.packageName);
                //添加AppName
                CharSequence sequence = resolveInfo.loadLabel(pm);
                if (sequence == null) {
                    appInfoBuilder.setAppName("");
                } else {
                    appInfoBuilder.setAppName(sequence.toString());
                }
                //添加VersionCode和VersionName
                appInfoBuilder.setVersionCode(packageInfo.versionCode);
                //注：有的系统应用versionName为null
                if (packageInfo.versionName == null) {
                    appInfoBuilder.setVersionName("");
                } else {
                    appInfoBuilder.setVersionName(packageInfo.versionName);
                }
                //添加icon url
                appInfoBuilder.setIconUrl(getIconUrl(localHost, packageInfo.packageName));

                builder.addList(appInfoBuilder.build());
            }

        }

        return builder.build();
    }

    /**
     * 获取单个应用信息
     *
     * @param packageName
     * @return
     */
    public static AppActionResponseProto.AppActionResponse getAppInfo(String packageName) {
        Context context = EMAcceptor.getInstance().getContext();
        PackageManager pm = context.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = pm.getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        AppActionResponseProto.AppActionResponse.Builder builder = AppActionResponseProto.AppActionResponse.newBuilder()
                .setMessageId(MID.getId())
                .setPackageName(packageName);

        if (packageInfo != null) {
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) { //非系统应用
                builder.setIsSystem(false);
            } else { // 系统应用
                builder.setIsSystem(true);
            }

            builder.setAppName(getAppName(context, packageName));
            builder.setVersionCode(packageInfo.versionCode);
            //注：有的系统应用versionName为null
            if (packageInfo.versionName == null) {
                builder.setVersionName("");
            } else {
                builder.setVersionName(packageInfo.versionName);
            }
        }

        return builder.build();
    }

    public static String getAppName(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        String name = "";
        try {
            name = pm.getApplicationLabel(pm.getApplicationInfo(packageName,
                    PackageManager.GET_META_DATA)).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return name;
    }

    /**
     * 获取icon的url
     *
     * @return
     */
    private static String getIconUrl(String localHost, String packageName) {
        StringBuilder builder = new StringBuilder();
        builder.append("http://").append(localHost).append(":").append(HttpServer.PORT)
                .append("/?action=dlicon&pkg=").append(packageName);
        return builder.toString();
    }

    /**
     * 打开APP
     *
     * @param packageName
     */
    public static void startApp(String packageName) {
        Context context = EMAcceptor.getInstance().getContext();
        if (context != null) {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    /**
     * 卸载APP
     *
     * @param packageName
     */
    public static void removApp(String packageName) {
        Context context = EMAcceptor.getInstance().getContext();
        if (context != null) {
            Intent intent = new Intent(Intent.ACTION_DELETE);
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("package:" + packageName));
            context.startActivity(intent);
        }
    }

    /**
     * 安装APP
     *
     * @param packageName
     * @param url
     */
    public static void installApp(String packageName, String url) {

    }

    /**
     * 打开设置页面
     */
    public static void openSetting() {
        Context context = EMAcceptor.getInstance().getContext();
        if (context != null) {
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    /**
     * 创建资源占用率响应body
     * @return
     */
    public static ResourceRateResponseProto.ResourceRateResponse getResourceRate(){
        ResourceRateResponseProto.ResourceRateResponse body = ResourceRateResponseProto.ResourceRateResponse.newBuilder()
                .setMessageId(MID.getId()).setResourceRate("99").build();

        return body;
    }
}
