package com.netty.server.utils;

import android.text.TextUtils;

/**
 * Created by robincxiao on 2017/9/4.
 */

public class HostUtils {
    public static String parseHost(String from) {
        if (!TextUtils.isEmpty(from)) {
            String[] array = from.split(":");
            return array[0].substring(1, array[0].length());
        }

        return "Null";
    }

    public static String parsePort(String from) {
        if (!TextUtils.isEmpty(from)) {
            String[] array = from.split(":");
            if(array.length > 1) {
                return array[1];
            }
        }

        return "Null";
    }

    public static String parseHostPort(String from) {
        if (!TextUtils.isEmpty(from)) {
            return from.substring(1, from.length());
        }

        return "";
    }

//    public static String[] parseHostAndPort(String from) {
//        if (!TextUtils.isEmpty(from)) {
//            String tmp = from.substring(1, from.length());
//            return tmp.split(":");
//        }
//
//        return null;
//    }
}
