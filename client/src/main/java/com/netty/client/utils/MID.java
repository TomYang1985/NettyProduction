package com.netty.client.utils;

import java.util.UUID;

/**
 * Created by robincxiao on 2017/9/11.
 */

public class MID {
    public static String getId(){
        return UUID.randomUUID().toString().replaceAll("-", "") + System.nanoTime();
    }
}
