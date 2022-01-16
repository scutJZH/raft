package com.jzh.raft.core2.model.schedule;

import lombok.Getter;
import lombok.Setter;

import java.util.Random;
import java.util.concurrent.*;

// todo 配置
public class ScheduleManagement {
    // 毫秒
    @Getter
    @Setter
    private Integer maxElectionTimeoutInterval;
    @Getter
    @Setter
    private Integer minElectionTimeoutInterval;
    @Getter
    @Setter
    private Integer replicationInterval;
    private final ScheduledExecutorService schedulePool;
    private final Random random;

    public ScheduleManagement(Integer maxElectionTimeoutInterval, Integer minElectionTimeoutInterval, Integer replicationInterval) {
        this.maxElectionTimeoutInterval = maxElectionTimeoutInterval;
        this.minElectionTimeoutInterval = minElectionTimeoutInterval;
        this.replicationInterval = replicationInterval;
        schedulePool = Executors.newSingleThreadScheduledExecutor();
        random = new Random();
    }

    public ElectionTimeoutSchedule generateElectionTimeoutSchedule(Runnable task) {
        ScheduledFuture<?> scheduledFuture = schedulePool.schedule(task, getRandomDelayTime(), TimeUnit.MILLISECONDS);
        return new ElectionTimeoutSchedule(scheduledFuture);
    }

    public LogReplicationSchedule generateLogReplicationSchedule(Runnable task) {
        ScheduledFuture<?> scheduledFuture = schedulePool.scheduleWithFixedDelay(task, 0, replicationInterval, TimeUnit.MILLISECONDS);
        return new LogReplicationSchedule(scheduledFuture);
    }

    private Long getRandomDelayTime() {
        return (long)(this.minElectionTimeoutInterval + random.nextInt(this.maxElectionTimeoutInterval - this.minElectionTimeoutInterval));
    }
}
