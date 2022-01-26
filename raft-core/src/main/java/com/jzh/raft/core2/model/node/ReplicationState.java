package com.jzh.raft.core2.model.node;

import lombok.Getter;
import lombok.Setter;

public class ReplicationState {
    @Getter
    @Setter
    private Integer nextIndex;

    @Getter
    @Setter
    private Integer matchIndex;

    public ReplicationState() {
        this.nextIndex = 0;
        this.matchIndex = 0;
    }
}
