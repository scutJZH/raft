package com.jzh.raft.core.model.rpc;

import lombok.Getter;

public class RequestVoteRpc {
    @Getter
    private final Long term;
    @Getter
    private final String candidateId;
    @Getter
    private final Long lastCommitLogIndex;
    @Getter
    private final Long lastCommitLogTerm;

    public RequestVoteRpc(Long term, String candidateId, Long lastCommitLogIndex, Long lastCommitLogTerm) {
        this.term = term;
        this.candidateId = candidateId;
        this.lastCommitLogIndex = lastCommitLogIndex;
        this.lastCommitLogTerm = lastCommitLogTerm;
    }
}
