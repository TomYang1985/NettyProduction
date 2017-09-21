package com.tencent.tvmanager.netty.common;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.tencent.tvmanager.netty.core.EMAcceptor;
import com.tencent.tvmanager.netty.innermsg.AppActionProto;
import com.tencent.tvmanager.netty.innermsg.AppListResponseProto;
import com.tencent.tvmanager.util.L;
import com.tencent.tvmanager.netty.util.MID;

import java.util.List;

/**
 * Created by robincxiao on 2017/9/18.
 */

/**
 * 业务逻辑帮助类
 */
public class BusinessHelper {
    /**
     * 获取应用列表
     *
     * @return
     */
//    public static AppListResponseProto.AppListResponse getPackages() {
//        // 获取已经安装的所有应用, PackageInfo　系统类，包含应用信息
//        Context context = EMAcceptor.getInstance().getContext();
//        AppListResponseProto.AppListResponse.Builder builder = AppListResponseProto.AppListResponse.newBuilder().setMessageId(MID.getId());
//
//        List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
//        for (int i = 0; i < packages.size(); i++) {
//            PackageInfo packageInfo = packages.get(i);
//
//            if (packageInfo != null) {
//                AppListResponseProto.AppInfo.Builder appInfoBuilder = AppListResponseProto.AppInfo.newBuilder();
//
//                if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) { //非系统应用
//                    appInfoBuilder.setIsSystem(false);
//                } else { // 系统应用
//                    appInfoBuilder.setIsSystem(true);
//                }
//
//                appInfoBuilder.setPackageName(packageInfo.packageName);
//                appInfoBuilder.setAppName(getAppName(context, packageInfo));
//                appInfoBuilder.setVersionCode(packageInfo.versionCode);
//                if(packageInfo.versionName == null){
//                    L.d(appInfoBuilder.getIsSystem() + "::" + packageInfo.packageName);
//                    appInfoBuilder.setVersionName("");
//                }else {
//                    appInfoBuilder.setVersionName(packageInfo.versionName);
//                }
//
//                builder.addList(appInfoBuilder.build());
//            }
//        }
//
//        return builder.build();
//    }

    /**
     * 获取已安装应用列表
     *
     * @return
     */
    public static AppListResponseProto.AppListResponse getPackages() {
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

                appInfoBuilder.setPackageName(packageInfo.packageName);
                CharSequence sequence = resolveInfo.loadLabel(pm);
                if (sequence == null) {
                    appInfoBuilder.setAppName("");
                } else {
                    appInfoBuilder.setAppName(sequence.toString());
                }
                appInfoBuilder.setVersionCode(packageInfo.versionCode);
                //注：有的系统应用versionName为null
                if (packageInfo.versionName == null) {
                    L.d(appInfoBuilder.getIsSystem() + "::" + packageInfo.packageName);
                    appInfoBuilder.setVersionName("");
                } else {
                    appInfoBuilder.setVersionName(packageInfo.versionName);
                }

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
    public static AppActionProto.AppAction getAppInfo(String packageName) {
        Context context = EMAcceptor.getInstance().getContext();
        PackageManager pm = context.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = pm.getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        AppActionProto.AppAction.Builder builder = AppActionProto.AppAction.newBuilder()
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

}
