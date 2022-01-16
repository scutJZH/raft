package com.jzh.raft.core2.model.schedule;

import java.util.concurrent.ScheduledFuture;

public class ElectionTimeoutSchedule implements ISchedule {

    private final ScheduledFuture<?> scheduledFuture;

    public ElectionTimeoutSchedule(ScheduledFuture<?> scheduledFuture) {
        this.scheduledFuture = scheduledFuture;
    }

    @Override
    public void cancel() {
        scheduledFuture.cancel(false);
    }
}
