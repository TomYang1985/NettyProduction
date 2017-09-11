package com.tencent.tvmanager.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

//Toast统一管理类
public class T {

    private T() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static boolean isShow = true;

    /**
     * 短时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showShort(Context context, CharSequence message) {
        if (isShow)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 短时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showShort(Context context, int message) {
        if (isShow)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 长时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showLong(Context context, CharSequence message) {
        if (isShow)
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    /**
     * 长时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showLong(Context context, int message) {
        if (isShow)
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    /**
     * 自定义显示Toast时间
     *
     * @param context
     * @param message
     * @param duration
     */
    public static void show(Context context, CharSequence message, int duration) {
        if (isShow)
            Toast.makeText(context, message, duration).show();
    }

    /**
     * 自定义显示Toast时间
     *
     * @param context
     * @param message
     * @param duration
     */
    public static void show(Context context, int message, int duration) {
        if (isShow)
            Toast.makeText(context, message, duration).show();
    }


    public static void showBottom(Context context, int messageId) {
        String message = context.getString(messageId);
        showBottom(context, message);
    }

    public static void showBottom(Context context, String msg) {
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void showMiddle(Context context, String msg) {
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void showShortCustomHint(Context context, String msg) {
        showCustomHint(context, msg, Toast.LENGTH_SHORT);
    }

    public static void showLongCustomHint(Context context, String msg) {
        showCustomHint(context, msg, Toast.LENGTH_LONG);
    }

    private static void showCustomHint(Context context, String msg, int duration) {
//        Toast toast = new Toast(context);
//        toast.setGravity(Gravity.BOTTOM, 0, DensityUtils.dp2px(context, 40));
//        toast.setDuration(duration);
//        View view = LayoutInflater.from(context).inflate(R.layout.toast, null);
//        ((TextView) view.findViewById(R.id.content)).setText(msg);
//        toast.setView(view);
//        toast.show();
    }
}