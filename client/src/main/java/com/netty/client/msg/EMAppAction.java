package com.netty.client.msg;

/**
 * Created by xiaoguochang on 2017/9/17.
 */

/**
 * 应用的安装、移除、更新返回的message
 */
public class EMAppAction extends EMMessage{
    public String appName;
    public String packageName;
    public int versionCode;
    public String versionName;
}
