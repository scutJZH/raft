package com.jzh.raft.core.model.node.role;

import com.jzh.raft.core.model.node.NodeRoleEnum;
import com.jzh.raft.core.model.schedule.LogReplicationSchedule;

public class LeaderNodeRole extends AbstractNodeRole {
    private final LogReplicationSchedule schedule;

    public LeaderNodeRole(Long term, LogReplicationSchedule schedule) {
        super(NodeRoleEnum.LEADER, term);
        this.schedule = schedule;
    }

    @Override
    public void stopScheduleTask() {
        schedule.cancel();
    }
}
