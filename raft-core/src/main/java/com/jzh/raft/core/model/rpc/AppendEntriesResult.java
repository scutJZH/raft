package com.jzh.raft.core.model.rpc;

import lombok.Getter;

public class AppendEntriesResult {
    @Getter
    private final Boolean result;

    public AppendEntriesResult(Boolean result) {
        this.result = result;
    }
}
