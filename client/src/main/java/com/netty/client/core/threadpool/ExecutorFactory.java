package com.netty.client.core.threadpool;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by xiaoguochang on 2017/8/27.
 */

public class ExecutorFactory {
    private volatile static ThreadPoolExecutor mRecvExecutor;
    private volatile static ThreadPoolExecutor mSendExecutor;
    private volatile static ThreadPoolExecutor mCallbackExecutor;

    public static void submitRecvTask(MessageRecvTask task){
        if(mRecvExecutor == null){
            synchronized (ExecutorFactory.class){
                if(mRecvExecutor == null){
                    mRecvExecutor = RpcThreadPool.getRecvHeavyExecutor(Runtime.getRuntime().availableProcessors() << 1);
                }
            }
        }

        mRecvExecutor.execute(task);
    }

    public static void submitSendTask(MessageSendTask task){
        if(mSendExecutor == null){
            synchronized (ExecutorFactory.class){
                if(mSendExecutor == null){
                    mSendExecutor = RpcThreadPool.getSendExecutor();
                }
            }
        }

        mSendExecutor.execute(task);
    }

    public static void submitCallbackTask(CallbackTask task){
        if(mCallbackExecutor == null){
            synchronized (ExecutorFactory.class){
                if(mCallbackExecutor == null){
                    mCallbackExecutor = RpcThreadPool.getCallbackExecutor();
                }
            }
        }

        mCallbackExecutor.execute(task);
    }

    public static void shutdownNow(){
        if(mRecvExecutor != null){
            mRecvExecutor.shutdownNow();
        }
    }
}
