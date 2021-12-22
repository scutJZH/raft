package com.jzh.raft.core.model.rpc.message;

import lombok.Getter;

public class RequestVoteRpcMsg {
    @Getter
    private final String candidateId;

    @Getter
    private final RequestVoteRpc requestVoteRpc;

    public RequestVoteRpcMsg(String candidateId, RequestVoteRpc requestVoteRpc) {
        this.candidateId = candidateId;
        this.requestVoteRpc = requestVoteRpc;
    }
}
