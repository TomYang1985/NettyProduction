package xiao.framework;

import android.app.Application;
import android.content.Context;

import xiao.framework.imageloader.ImageLoadTool;

/**
 * Created by robincxiao on 2017/3/14.
 */

public class FrameworkApi {
    public static String TAG = "framework";
    public static Context sAppContext;
    public static boolean isDebug;

    public static void init(FrameworkSdkCallback callback){
        if(callback != null) {
            sAppContext = callback.getApplication();
            isDebug = callback.isDebug();
            ImageLoadTool.getInstance().init(sAppContext);
        }
    }

    public interface FrameworkSdkCallback{
        Application getApplication();

        boolean isDebug();
    }
}
