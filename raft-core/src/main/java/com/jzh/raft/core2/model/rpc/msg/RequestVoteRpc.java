package com.jzh.raft.core2.model.rpc.msg;

import lombok.Getter;

public class RequestVoteRpc {
    @Getter
    private final String nodeId;
    @Getter
    private final Integer term;
    @Getter
    private final Long lastCommittedLogEntryTerm;
    @Getter
    private final Long lastCommittedLastLogEntryIndex;

    public RequestVoteRpc(String nodeId, Integer term, Long lastCommittedLogEntryTerm, Long lastCommittedLastLogEntryIndex) {
        this.nodeId = nodeId;
        this.term = term;
        this.lastCommittedLogEntryTerm = lastCommittedLogEntryTerm;
        this.lastCommittedLastLogEntryIndex = lastCommittedLastLogEntryIndex;
    }
}
