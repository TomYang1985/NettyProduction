package com.netty.client.core.threadpool;

import java.util.concurrent.Executor;
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
    public static ThreadPoolExecutor getRecvHeavyExecutor(int threads){
        String name = "RpcClientRecvPoll";
        return new ThreadPoolExecutor(threads, threads, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(),
                new NamedThreadFactory(name, true), new AbortPolicyWithReport(name));
    }

    /**
     * 消息发送线程池
     * @return
     */
    public static ThreadPoolExecutor getSendExecutor(){
        String name = "RpcClientSendPoll";
        return new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(),
                new NamedThreadFactory(name, true), new AbortPolicyWithReport(name));
    }

    /**
     * 消息UI回调线程池
     * @return
     */
    public static ThreadPoolExecutor getCallbackExecutor(){
        String name = "RpcClientUICallbackPoll";
        return new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(),
                new NamedThreadFactory(name, true), new AbortPolicyWithReport(name));
    }
}
