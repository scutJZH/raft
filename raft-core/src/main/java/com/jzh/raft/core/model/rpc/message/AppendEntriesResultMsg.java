package com.jzh.raft.core.model.rpc.message;

import lombok.Getter;

public class AppendEntriesResultMsg {
    @Getter
    private final String nodeId;

    @Getter
    private final AppendEntriesResult appendEntriesResult;

    public AppendEntriesResultMsg(String nodeId, AppendEntriesResult appendEntriesResult) {
        this.nodeId = nodeId;
        this.appendEntriesResult = appendEntriesResult;
    }
}
