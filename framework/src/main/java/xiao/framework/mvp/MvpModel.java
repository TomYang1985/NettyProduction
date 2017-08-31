package xiao.framework.mvp;

import android.app.Activity;
import android.content.Context;

import java.lang.ref.WeakReference;

/**
 * Created by robincxiao on 2016/11/23.
 */

public abstract class MvpModel {
    protected Context mContext;
    protected WeakReference<Activity> mActivityWeakReference;

    public MvpModel(Context context) {
        this.mContext = context;

        if(context instanceof Activity) {
            mActivityWeakReference = new WeakReference<>((Activity) context);
        }
    }

    /**
     * 在UI线程中运行
     * @param runnable
     */
    protected void runOnUiThread(Runnable runnable){
        if (mActivityWeakReference != null && mActivityWeakReference.get() != null) {
            mActivityWeakReference.get().runOnUiThread(runnable);
        }
    }
}
