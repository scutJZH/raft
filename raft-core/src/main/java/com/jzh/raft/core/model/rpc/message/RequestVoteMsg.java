package com.jzh.raft.core.model.rpc.message;

import lombok.Getter;

public class RequestVoteMsg {
    @Getter
    private final String candidateId;

    @Getter
    private final RequestVoteRpc requestVoteRpc;

    public RequestVoteMsg(String candidateId, RequestVoteRpc requestVoteRpc) {
        this.candidateId = candidateId;
        this.requestVoteRpc = requestVoteRpc;
    }
}
