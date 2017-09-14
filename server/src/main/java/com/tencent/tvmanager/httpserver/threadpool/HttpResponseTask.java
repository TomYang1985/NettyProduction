package com.tencent.tvmanager.httpserver.threadpool;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * Created by robincxiao on 2017/9/14.
 */

public class HttpResponseTask implements Runnable{
    private ChannelHandlerContext ctx;
    private FullHttpRequest request;

    public HttpResponseTask(ChannelHandlerContext ctx, FullHttpRequest request) {
        this.ctx = ctx;
        this.request = request;
    }

    @Override
    public void run() {

    }
}
