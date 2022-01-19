package com.jzh.raft.core2.model.node.role;

import com.jzh.raft.core2.model.schedule.LogReplicationSchedule;

public class LeaderRole extends AbsNodeRole {
    private final LogReplicationSchedule schedule;

    public LeaderRole(Integer currentTerm, LogReplicationSchedule schedule) {
        super(currentTerm, RoleTypeEnum.LEADER);
        this.schedule = schedule;
    }

    @Override
    public void stopScheduleTask() {
        schedule.cancel();
    }
}
