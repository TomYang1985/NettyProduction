package com.netty.client.core.threadpool;


import com.netty.client.utils.L;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by xiaoguochang on 2017/8/26.
 */

public class AbortPolicyWithReport extends ThreadPoolExecutor.AbortPolicy{
    private String threadName;

    public AbortPolicyWithReport(String threadName){
        this.threadName = threadName;
    }


    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        String msg = String.format("RpcServer["
                        + " Thread Name: %s, Pool Size: %d (active: %d, core: %d, max: %d, largest: %d), Task: %d (completed: %d),"
                        + " Executor status:(isShutdown:%s, isTerminated:%s, isTerminating:%s)]",
                threadName, e.getPoolSize(), e.getActiveCount(), e.getCorePoolSize(), e.getMaximumPoolSize(), e.getLargestPoolSize(),
                e.getTaskCount(), e.getCompletedTaskCount(), e.isShutdown(), e.isTerminated(), e.isTerminating());
        L.print(msg);
    }
}
