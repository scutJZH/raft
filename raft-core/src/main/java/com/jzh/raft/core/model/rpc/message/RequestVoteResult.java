package com.jzh.raft.core.model.rpc.message;

import lombok.Getter;

public class RequestVoteResult {
    @Getter
    private final Boolean result;
    @Getter
    private final Long currentTerm;

    public RequestVoteResult(Boolean result, Long currentTerm) {
        this.result = result;
        this.currentTerm = currentTerm;
    }
}
