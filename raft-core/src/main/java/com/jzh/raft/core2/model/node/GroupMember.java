package com.jzh.raft.core2.model.node;

import lombok.Getter;
import lombok.NonNull;

public class GroupMember {
    @Getter
    private final NodeEndPoint nodeEndPoint;

    private final ReplicationState replicationState;

    public GroupMember(@NonNull NodeEndPoint nodeEndPoint) {
        this.nodeEndPoint = nodeEndPoint;
        this.replicationState = new ReplicationState();
    }

    public Integer getNextIndex() {
        return this.replicationState.getNextIndex();
    }

    public Integer getMatchIndex() {
        return this.replicationState.getMatchIndex();
    }

    public void setNextIndex(Integer nextIndex) {
        this.replicationState.setNextIndex(nextIndex);
    }

    public void setMatchIndex(Integer matchIndex) {
        this.replicationState.setMatchIndex(matchIndex);
    }

}
