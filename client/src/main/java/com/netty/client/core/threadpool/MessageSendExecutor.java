package com.netty.client.core.threadpool;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by xiaoguochang on 2017/8/27.
 */

public class MessageSendExecutor {
    private static ThreadPoolExecutor mExecutor;

    public static void submit(MessageSendTask task){
        if(mExecutor == null){
            synchronized (MessageSendExecutor.class){
                if(mExecutor == null){
                    mExecutor = RpcThreadPool.getSendExecutor();
                }
            }
        }

        mExecutor.execute(task);
    }

    public static void shutdownNow(){
        if(mExecutor != null){
            mExecutor.shutdownNow();
        }
    }
}
