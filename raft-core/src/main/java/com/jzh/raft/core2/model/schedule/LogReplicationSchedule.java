package com.jzh.raft.core2.model.schedule;

import java.util.concurrent.ScheduledFuture;

public class LogReplicationSchedule implements ISchedule {

    private final ScheduledFuture<?> scheduledFuture;

    public LogReplicationSchedule(ScheduledFuture<?> scheduledFuture) {
        this.scheduledFuture = scheduledFuture;
    }

    @Override
    public void cancel() {
        scheduledFuture.cancel(false);
    }
}
