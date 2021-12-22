package com.jzh.raft.core.model.rpc.message;

import lombok.Getter;

public class AppendEntriesResult {
    @Getter
    private final Boolean result;

    @Getter
    private final Long term;

    public AppendEntriesResult(Boolean result, Long term) {
        this.result = result;
        this.term = term;
    }
}
