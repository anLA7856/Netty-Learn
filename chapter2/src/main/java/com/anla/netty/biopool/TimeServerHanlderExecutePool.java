package com.anla.netty.biopool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @user anLA7856
 * @time 19-1-9 下午10:22
 * @description 线程池
 */
public class TimeServerHanlderExecutePool {
    private ExecutorService executor;

    public TimeServerHanlderExecutePool(int maxPoolSIze, int queueSize) {
        executor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), maxPoolSIze, 120L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(queueSize));
    }

    public void execute(Runnable task){
        executor.execute(task);
    }
}
