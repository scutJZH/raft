package com.jzh.raft.core.model.node.role;

import com.jzh.raft.core.model.node.NodeRoleEnum;
import com.jzh.raft.core.model.schedule.ElectionSchedule;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public class FollowerNodeRole extends AbstractNodeRole {
    @Getter
    @Setter
    private String votedFor;

    @Getter
    @Setter
    private String leaderId;

    private final ElectionSchedule schedule;

    public FollowerNodeRole(Long term, String leaderId, String votedFor, @NonNull ElectionSchedule schedule) {
        super(NodeRoleEnum.FOLLOWER, term);
        this.leaderId = leaderId;
        this.votedFor = votedFor;
        this.schedule = schedule;
    }

    @Override
    public void stopScheduleTask() {
        schedule.cancel();
    }
}
