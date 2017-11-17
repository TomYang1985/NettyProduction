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

import android.os.Handler;
import android.os.Message;

import com.netty.client.common.ETvModelID;
import com.netty.client.utils.L;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.BindException;
import java.net.SocketException;
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
    private static final int DEFAULT_PORT = 8443;
    private static final int MAX_TRY_COUNT = 2;//最大尝试次数
    private static final int STATUS_NONE = 1;
    private static final int STATUS_BINDING = 2;
    private static final int STATUS_BINDED = 3;
    private static final int MSG_RETRY = 0;
    private int mPort = DEFAULT_PORT;
    private volatile static HttpServer sInstance;
    private EventLoopGroup mBossGroup;
    private EventLoopGroup mWorkerGroup;
    private Object mLock = new Object();;
    private ServerBootstrap mServerBootstrap;
    private boolean isInited = false;
    private int mBindStatus = STATUS_NONE;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if(++mPort <= DEFAULT_PORT + MAX_TRY_COUNT){
                start();
            }else {
                mPort = DEFAULT_PORT;
            }
            return true;
        }
    });

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

    private void init(){
        mBossGroup = new NioEventLoopGroup(1);
        mWorkerGroup = new NioEventLoopGroup();

        mServerBootstrap = new ServerBootstrap();
        mServerBootstrap.group(mBossGroup, mWorkerGroup)
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
    }

    public void start() {
        synchronized (mLock){
            if(mBindStatus == STATUS_BINDED || mBindStatus == STATUS_BINDING){
                L.writeFile("mBindStatus is binding or binded return");
                return;
            }

            mBindStatus = STATUS_BINDING;

            if(!isInited){
                isInited = true;
                init();
            }
        }

        mServerBootstrap.bind(mPort).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    synchronized (mLock){
                        mBindStatus = STATUS_BINDED;
                    }
                    L.writeFile("HttpFileServer bind succ port = " + mPort);
                    L.print("HttpFileServer bind succ port = " + mPort);
                } else {
                    L.writeFile("HttpFileServer bind fail");
                    L.print("HttpFileServer bind fail = " + mPort);

                    //当端口数已经测试到不可用后，记录统计点
                    if(mPort == DEFAULT_PORT + MAX_TRY_COUNT) {
                        ETvModelID.saveActionData(ETvModelID.EMID_Secure_RemoteControl_TVcommunication_HTTP_SERVER_Bind_Fail);
                        Throwable throwable = channelFuture.cause();
                        if (throwable != null) {
                            if (throwable instanceof BindException) {//bind异常(bind failed: EADDRINUSE (Address already in use))
                                L.writeFile("HttpFileServer BindException Address already in use");
                                ETvModelID.saveActionData(ETvModelID.EMID_Secure_RemoteControl_TVcommunication_HTTP_SERVER_Address_Already_In_Use);
                            } else if (throwable instanceof SocketException) {
                                String cause = throwable.toString();
                                if (cause != null && cause.contains("EADDRINUSE")) {
                                    ETvModelID.saveActionData(ETvModelID.EMID_Secure_RemoteControl_TVcommunication_HTTP_SERVER_Address_Already_In_Use);
                                    L.writeFile("HttpFileServer SocketException Address already in use");
                                } else {
                                    L.writeFile("HttpFileServer bind fail : " + throwable.toString());
                                }
                            } else {
                                L.writeFile("HttpFileServer bind fail : " + throwable.toString());
                            }
                        }
                    }

                    //放在业务逻辑后面执行
                    synchronized (mLock){
                        mBindStatus = STATUS_NONE;
                    }
                    //需要放在设置STATUS_NONE后执行
                    if(mHandler != null) {
                        mHandler.sendEmptyMessage(MSG_RETRY);
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
            builder.append("http://").append(ip).append(":").append(mPort).append(File.separator).append(URLEncoder.encode(path, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return builder.toString();
    }

    public void destory(){
        sInstance = null;
        mHandler.removeMessages(MSG_RETRY);
        mHandler = null;
        mBossGroup.shutdownGracefully();
        mWorkerGroup.shutdownGracefully();
    }
}
