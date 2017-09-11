package com.tencent.tvmanager.netty.core;

/**
 * Created by robincxiao on 2017/8/23.
 */

public interface Acceptor {
    void start();

    void shutdownGracefully();
}
