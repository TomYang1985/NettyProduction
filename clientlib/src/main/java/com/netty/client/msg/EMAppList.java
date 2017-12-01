package com.netty.client.msg;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaoguochang on 2017/9/17.
 */

public class EMAppList extends EMMessage {
    private List<AppInfo> appInfos = new ArrayList<>();

    public void add(String packageName, String appName, int versionCode, String versionName, boolean isSystem,
                    String iconUrl, long size, long firstInstallTime) {
        appInfos.add(new EMAppList.AppInfo(packageName,
                appName, versionCode, versionName, isSystem, iconUrl, size, firstInstallTime));
    }

    public List<AppInfo> getAppInfos() {
        return appInfos;
    }

    public class AppInfo {
        public String packageName;
        public String appName;
        public int versionCode;
        public String versionName;
        public boolean isSystem;
        public String iconUrl;
        public long size;
        public long firstInstallTime;

        public AppInfo(String packageName, String appName, int versionCode, String versionName, boolean isSystem,
                       String iconUrl, long size, long firstInstallTime) {
            msgType = MSG_TYPE_APP_LIST;
            this.packageName = packageName;
            this.appName = appName;
            this.versionCode = versionCode;
            this.versionName = versionName;
            this.isSystem = isSystem;
            this.iconUrl = iconUrl;
            this.size = size;
            this.firstInstallTime = firstInstallTime;
        }
    }
}
