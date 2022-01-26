package com.jzh.raft.core2.model.node.role;

import com.jzh.raft.core2.model.schedule.ElectionTimeoutSchedule;
import lombok.Getter;
import lombok.Setter;

public class CandidateRole extends AbsNodeRole {
    @Getter
    @Setter
    private Integer voteCount;

    private final ElectionTimeoutSchedule schedule;

    public CandidateRole(Integer currentTerm, ElectionTimeoutSchedule schedule) {
        super(currentTerm, RoleTypeEnum.CANDIDATE);
        this.schedule = schedule;
        voteCount = 0;
    }

    @Override
    public void stopScheduleTask() {
        if (this.schedule != null) {
            schedule.cancel();
        }
    }
}
