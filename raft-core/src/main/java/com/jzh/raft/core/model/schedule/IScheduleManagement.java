package com.jzh.raft.core.model.schedule;

public interface IScheduleManagement {
    LogReplicationSchedule generateLogReplicationSchedule(Runnable task);

    ElectionSchedule generateElectionSchedule(Runnable task);

    void stop() throws InterruptedException;
}
