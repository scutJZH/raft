package com.jzh.raft.core2.model.node.role;

import com.jzh.raft.core2.model.schedule.ElectionTimeoutSchedule;
import lombok.Getter;

public class FollowerRole extends AbsNodeRole {

    @Getter
    private final String voteFor;

    @Getter
    private final ElectionTimeoutSchedule schedule;

    public FollowerRole(Integer currentTerm, String voteFor, ElectionTimeoutSchedule schedule) {
        super(currentTerm, RoleTypeEnum.FOLLOWER);
        this.voteFor = voteFor;
        this.schedule = schedule;
    }

    @Override
    public void stopScheduleTask() {
        this.schedule.cancel();
    }
}
