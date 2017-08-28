package com.netty.server.core.threadpool;

import java.util.concurrent.Executor;

/**
 * Created by xiaoguochang on 2017/8/27.
 */

public class MessageSendExecutor {
    private static Executor mSendExecutor;

    public static void submit(MessageSendTask task){
        if(mSendExecutor == null){
            synchronized (MessageSendExecutor.class){
                if(mSendExecutor == null){
                    mSendExecutor = RpcThreadPool.getSendExecutor();
                }
            }
        }

        mSendExecutor.execute(task);
    }
}
