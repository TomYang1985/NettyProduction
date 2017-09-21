package com.netty.client.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by xiaoguochang on 2016/1/9.
 */
public class GsonUtils {
    public static <T> T genarateBean(String str, Type type) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(str, type);
    }

    public static <T> List<T> genarateListBean(String str) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(str, new TypeToken<List<T>>() {
        }.getType());
    }

    public static String toJson(Object object) {
        return newInstance().toJson(object);
    }

    public static Gson newInstance() {
        GsonBuilder builder = new GsonBuilder();

        return builder.create();
    }
}
