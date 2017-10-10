package com.netty.client.httpserver.threadpool;


import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by xiaoguochang on 2017/8/27.
 */

public class HttpServerExecutorFactory {
    private volatile static ThreadPoolExecutor mResponseExecutor;

    public static void submitResponseTask(HttpResponseTask task) {
        if (mResponseExecutor == null) {
            synchronized (HttpServerExecutorFactory.class) {
                if (mResponseExecutor == null) {
                    String name = "HttpServerPoll";
                    mResponseExecutor = new ThreadPoolExecutor(1, Runtime.getRuntime().availableProcessors() << 1, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(),
                            new NamedThreadFactory(name, true), new AbortPolicyWithReport(name));
                }
            }
        }

        mResponseExecutor.execute(task);
    }


    public static void shutdownNow() {
        if (mResponseExecutor != null) {
            mResponseExecutor.shutdownNow();
        }
    }
}
