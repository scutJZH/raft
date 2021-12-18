package com.jzh.raft.core.model.schedule;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ScheduleManagement implements IScheduleManagement {

    private final Integer minElectionInterval;
    private final Integer maxElectionInterval;
    private final Integer logReplicationDelay;
    private final Integer logReplicationInterval;
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private final Random random = new Random();

    public ScheduleManagement(Integer minElectionInterval, Integer maxElectionInterval, Integer logReplicationDelay,
                              Integer logReplicationInterval) {
        if (minElectionInterval < 1 || minElectionInterval >= maxElectionInterval
                || logReplicationDelay < 0 || logReplicationInterval < 1) {
            throw new IllegalArgumentException("params invalid");
        }
        this.minElectionInterval = minElectionInterval;
        this.maxElectionInterval = maxElectionInterval;
        this.logReplicationDelay = logReplicationDelay;
        this.logReplicationInterval = logReplicationInterval;
    }

    @Override
    public LogReplicationSchedule generateLogReplicationSchedule(Runnable task) {
        ScheduledFuture<?> scheduledFuture = this.scheduledExecutorService.scheduleWithFixedDelay(task, logReplicationDelay, logReplicationInterval, TimeUnit.MILLISECONDS);
        return null;
    }

    @Override
    public ElectionSchedule generateElectionSchedule(Runnable task) {
        int randomDelay = generateRandomDelayMillisecond();
        ScheduledFuture<?> scheduledFuture = this.scheduledExecutorService.schedule(task, randomDelay, TimeUnit.MILLISECONDS);
        return new ElectionSchedule(scheduledFuture);
    }

    @Override
    public void stop() throws InterruptedException {
        this.scheduledExecutorService.shutdown();
        this.scheduledExecutorService.awaitTermination(maxElectionInterval, TimeUnit.MILLISECONDS);
    }

    private Integer generateRandomDelayMillisecond() {
        return this.minElectionInterval + random.nextInt((this.maxElectionInterval - this.minElectionInterval));
    }
}
