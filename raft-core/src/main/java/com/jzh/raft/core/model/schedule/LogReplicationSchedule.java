package com.jzh.raft.core.model.schedule;

import java.util.concurrent.ScheduledFuture;

public class LogReplicationSchedule {
    private final ScheduledFuture<?> scheduledFuture;

    public LogReplicationSchedule(ScheduledFuture<?> scheduledFuture) {
        this.scheduledFuture = scheduledFuture;
    }

    public void cancel() {
        scheduledFuture.cancel(false);
    }
}
