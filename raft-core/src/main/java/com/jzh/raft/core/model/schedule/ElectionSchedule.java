package com.jzh.raft.core.model.schedule;

import java.util.concurrent.ScheduledFuture;

public class ElectionSchedule {
    private final ScheduledFuture<?> scheduledFuture;

    public ElectionSchedule(ScheduledFuture<?> scheduledFuture) {
        this.scheduledFuture = scheduledFuture;
    }

    public void cancel() {
        scheduledFuture.cancel(false);
    }
}
