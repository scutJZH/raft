package com.jzh.raft.core.model.node.role;

import com.jzh.raft.core.model.node.NodeRoleEnum;
import com.jzh.raft.core.model.schedule.ElectionSchedule;
import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CandidateNodeRole extends AbstractNodeRole {
    private final ElectionSchedule schedule;

    @Getter
    private final Set<String> votedNodeIdSet;

    public CandidateNodeRole(@NonNull Long term, @NonNull ElectionSchedule schedule) {
        this(term, schedule, new HashSet<>());
    }

    public CandidateNodeRole(@NonNull Long term, @NonNull ElectionSchedule schedule, Set<String> votedNodeIdSet) {
        super(NodeRoleEnum.CANDIDATE, term);
        this.schedule = schedule;
        this.votedNodeIdSet = votedNodeIdSet;
    }

    @Override
    public void stopScheduleTask() {
        schedule.cancel();
    }

    public synchronized void voteCountPlusOne(String NodeId) {
        this.votedNodeIdSet.add(NodeId);
    }

    public Integer getVoteCount() {
        return this.votedNodeIdSet.size();
    }
}
