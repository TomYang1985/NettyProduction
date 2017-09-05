package com.netty.server.core;

import java.net.SocketAddress;

/**
 * Created by robincxiao on 2017/8/23.
 */

public interface Acceptor {
    void start();

    void shutdownGracefully();
}
