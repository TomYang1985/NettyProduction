package com.netty.client.core;

/**
 * Created by robincxiao on 2017/8/24.
 */

public interface ClientConnector {
    void connect();
    void shutdownGracefully();
}
