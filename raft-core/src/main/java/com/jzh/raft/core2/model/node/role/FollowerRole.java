package com.jzh.raft.core2.model.node.role;

import com.jzh.raft.core2.model.node.NodeId;
import com.jzh.raft.core2.model.schedule.ElectionTimeoutSchedule;
import lombok.Getter;

public class FollowerRole extends AbsNodeRole {

    @Getter
    private final NodeId voteFor;

    @Getter
    private final ElectionTimeoutSchedule schedule;

    public FollowerRole(Integer currentTerm, NodeId voteFor, ElectionTimeoutSchedule schedule) {
        super(currentTerm, RoleTypeEnum.FOLLOWER);
        this.voteFor = voteFor;
        this.schedule = schedule;
    }

    @Override
    public void stopScheduleTask() {
        if (this.schedule != null) {
            schedule.cancel();
        }
    }
}
