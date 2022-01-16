package com.jzh.raft.core2.model.node.role;

import com.jzh.raft.core2.model.schedule.ElectionTimeoutSchedule;

public class CandidateRole extends AbsNodeRole {
    private final ElectionTimeoutSchedule schedule;

    public CandidateRole(Integer currentTerm, ElectionTimeoutSchedule schedule) {
        super(currentTerm, RoleTypeEnum.CANDIDATE);
        this.schedule = schedule;
    }

    @Override
    public void stopScheduleTask() {
        schedule.cancel();
    }
}
