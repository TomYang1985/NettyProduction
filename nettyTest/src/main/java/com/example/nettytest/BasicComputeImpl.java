package com.example.nettytest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.netty.util.concurrent.DefaultPromise;

/**
 * Created by robincxiao on 2017/11/2.
 */

public class BasicComputeImpl {
    private DefaultPromise future;
    // 执行异步任务的线程池
    private ExecutorService executor = Executors.newCachedThreadPool();

    public BasicComputeImpl(DefaultPromise future) {
        this.future = future;
    }


    public DefaultPromise add(final int a, final int b) {

        executor.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + ",start to compute......");
                int result = a + b;
                System.out.println(Thread.currentThread().getName() + ",got the result:" + result);
                future.setSuccess(result);

            }
        });

        return future;
    }
}
