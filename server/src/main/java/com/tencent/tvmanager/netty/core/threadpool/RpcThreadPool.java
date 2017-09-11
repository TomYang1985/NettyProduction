package com.tencent.tvmanager.netty.core.threadpool;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by xiaoguochang on 2017/8/26.
 */

public class RpcThreadPool {
    /**
     * 消息接收线程池
     * @param threads
     * @return
     */
    public static ThreadPoolExecutor getRecvExecutor(int threads){
        String name = "RpcServerRecvPoll";
        return new ThreadPoolExecutor(threads, threads, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(),
                new NamedThreadFactory(name, true), new AbortPolicyWithReport(name));
    }

    /**
     * 消息发送线程池
     * @return
     */
    public static ThreadPoolExecutor getSendExecutor(){
        String name = "RpcServerSendPoll";
        return new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(),
                new NamedThreadFactory(name, true), new AbortPolicyWithReport(name));
    }

    /**
     * 消息UI回调线程池
     * @return
     */
    public static ThreadPoolExecutor getCallbackExecutor(){
        String name = "RpcServerUICallbackPoll";
        return new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(),
                new NamedThreadFactory(name, true), new AbortPolicyWithReport(name));
    }
}
