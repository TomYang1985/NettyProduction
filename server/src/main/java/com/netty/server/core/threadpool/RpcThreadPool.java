package com.netty.server.core.threadpool;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by xiaoguochang on 2017/8/26.
 */

public class RpcThreadPool {
    public static Executor getRecvQuickExecutor(){
        String name = "RpcServerQuickPoll";
        return new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(),
                new NamedThreadFactory(name, true), new AbortPolicyWithReport(name));
    }

    public static Executor getRecvHeavyExecutor(int threads){
        String name = "RpcServerHeavyPoll";
        return new ThreadPoolExecutor(threads, threads, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(),
                new NamedThreadFactory(name, true), new AbortPolicyWithReport(name));
    }

    public static Executor getSendExecutor(){
        String name = "RpcServerSendPoll";
        return new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(),
                new NamedThreadFactory(name, true), new AbortPolicyWithReport(name));
    }


}
