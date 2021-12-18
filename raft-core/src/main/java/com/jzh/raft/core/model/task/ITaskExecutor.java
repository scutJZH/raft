package com.jzh.raft.core.model.task;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public interface ITaskExecutor {
    <V> Future<V> submit(Callable<V> task);

    Future<?> submit(Runnable task);

    void shutdown() throws InterruptedException;
}
