package com.netty.server.core;

import java.net.SocketAddress;

/**
 * Created by robincxiao on 2017/8/23.
 */

public interface ServerAcceptor {
    void start();

    void shutdownGracefully();
}
