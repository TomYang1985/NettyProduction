package com.netty.client.core.threadpool;

import java.util.concurrent.Executor;

/**
 * Created by xiaoguochang on 2017/8/27.
 */

public class MessageRecvExecutor {
    private static Executor mExecutor;

    public static void submit(MessageRecvTask task){
        if(mExecutor == null){
            synchronized (MessageRecvExecutor.class){
                if(mExecutor == null){
                    mExecutor = RpcThreadPool.getRecvHeavyExecutor(Runtime.getRuntime().availableProcessors() << 1);
                }
            }
        }

        mExecutor.execute(task);
    }
}
