package com.jzh.raft.core2.model.rpc.msg;

import com.jzh.raft.core2.model.node.NodeId;
import lombok.Getter;

public class RequestVoteRpc {
    @Getter
    private final NodeId nodeId;
    @Getter
    private final Integer term;
    @Getter
    private final Integer lastCommittedLogEntryTerm;
    @Getter
    private final Long lastCommittedLastLogEntryIndex;

    public RequestVoteRpc(NodeId nodeId, Integer term, Integer lastCommittedLogEntryTerm, Long lastCommittedLastLogEntryIndex) {
        this.nodeId = nodeId;
        this.term = term;
        this.lastCommittedLogEntryTerm = lastCommittedLogEntryTerm;
        this.lastCommittedLastLogEntryIndex = lastCommittedLastLogEntryIndex;
    }
}
