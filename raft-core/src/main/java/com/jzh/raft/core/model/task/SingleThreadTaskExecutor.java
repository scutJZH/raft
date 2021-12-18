package com.jzh.raft.core.model.task;

import java.util.concurrent.*;

public class SingleThreadTaskExecutor implements ITaskExecutor {
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Integer awaitTerminationInterval;

    public SingleThreadTaskExecutor() {
        this(0);
    }

    public SingleThreadTaskExecutor(Integer awaitTerminationInterval) {
        this.awaitTerminationInterval = awaitTerminationInterval;
    }

    @Override
    public <V> Future<V> submit(Callable<V> task) {
        return executorService.submit(task);
    }

    @Override
    public Future<?> submit(Runnable task) {
        return executorService.submit(task);
    }

    @Override
    public void shutdown() throws InterruptedException {
        executorService.shutdown();
        if (this.awaitTerminationInterval > 0) {
            executorService.awaitTermination(awaitTerminationInterval, TimeUnit.MILLISECONDS);
        }
    }
}
