package com.jzh.raft.core.model.node.role;

import com.jzh.raft.core.model.node.NodeRoleEnum;
import com.jzh.raft.core.model.schedule.ElectionSchedule;
import lombok.NonNull;

public class CandidateNodeRole extends AbstractNodeRole {

    private Integer voteCount;
    private final ElectionSchedule schedule;

    public CandidateNodeRole(Long term, @NonNull ElectionSchedule schedule) {
        super(NodeRoleEnum.CANDIDATE, term);
        this.schedule = schedule;
    }

    @Override
    public void stopScheduleTask() {
        schedule.cancel();
    }
}
