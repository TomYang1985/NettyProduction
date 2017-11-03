/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.netty.client.httpserver;

import com.netty.client.common.ETvModelID;
import com.netty.client.utils.L;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.BindException;
import java.net.URLEncoder;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public final class HttpServer {
    public static final int PORT = 8443;
    private volatile static HttpServer sInstance;
    private EventLoopGroup mBossGroup;
    private EventLoopGroup mWorkerGroup;

    private HttpServer() {

    }

    public static HttpServer getInstance() {
        if (sInstance == null) {
            synchronized (HttpServer.class) {
                if (sInstance == null) {
                    sInstance = new HttpServer();
                }
            }
        }

        return sInstance;
    }

    public void start() {
        mBossGroup = new NioEventLoopGroup(1);
        mWorkerGroup = new NioEventLoopGroup();

        ServerBootstrap b = new ServerBootstrap();
        b.group(mBossGroup, mWorkerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new HttpServerCodec());
                        pipeline.addLast(new HttpObjectAggregator(65536));
                        pipeline.addLast(new ChunkedWriteHandler());
                        pipeline.addLast(new HttpServerHandler());
                    }
                });

        b.bind(PORT).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    L.writeFile("HttpFileServer bind succ");
                } else {
                    L.writeFile("HttpFileServer bind fail");
                    ETvModelID.saveActionData(ETvModelID.EMID_Secure_RemoteControl_TVcommunication_HTTP_SERVER_Bind_Fail);
                    Throwable throwable = channelFuture.cause();
                    if (throwable != null) {
                        if (throwable instanceof BindException) {//bind异常(bind failed: EADDRINUSE (Address already in use))
                            L.writeFile("HttpFileServer BindException Address already in use");
                            ETvModelID.saveActionData(ETvModelID.EMID_Secure_RemoteControl_TVcommunication_HTTP_SERVER_Address_Already_In_Use);
                        } else {
                            L.writeFile("HttpFileServer bind fail : " + throwable.toString());
                        }
                    }
                }
            }
        });
    }

    /**
     * 获取本地文件url
     * @param ip 本地ip
     * @param path 本地文件路径
     * @return
     */
    public String getLocalUrl(String ip, String path){
        StringBuilder builder = new StringBuilder();
        try {
            //拼接的URL中不能有中文，所以对path进行URLEncoder
            builder.append("http://").append(ip).append(":").append(PORT).append(File.separator).append(URLEncoder.encode(path, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return builder.toString();
    }

    public void destory(){
        sInstance = null;
        mBossGroup.shutdownGracefully();
        mWorkerGroup.shutdownGracefully();
    }
}
