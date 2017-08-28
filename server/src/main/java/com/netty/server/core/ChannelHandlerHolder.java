package com.netty.server.core;

import io.netty.channel.ChannelHandler;

/**
 * Created by robincxiao on 2017/8/21.
 */

public interface ChannelHandlerHolder {
    ChannelHandler[] handlers();
}
