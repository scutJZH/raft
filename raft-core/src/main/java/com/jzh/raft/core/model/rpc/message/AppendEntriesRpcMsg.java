package com.jzh.raft.core.model.rpc.message;

import lombok.Getter;

public class AppendEntriesRpcMsg {
    @Getter
    private final String leaderId;

    @Getter
    private final AppendEntriesRpc appendEntriesRpc;

    public AppendEntriesRpcMsg(String leaderId, AppendEntriesRpc appendEntriesRpc) {
        this.leaderId = leaderId;
        this.appendEntriesRpc = appendEntriesRpc;
    }
}
