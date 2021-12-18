package com.jzh.raft.core.model.rpc;

import lombok.Getter;

public class RequestVoteResult {
    @Getter
    private final Boolean result;

    public RequestVoteResult(Boolean result) {
        this.result = result;
    }
}
