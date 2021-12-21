package com.jzh.raft.core.model.rpc.message;

import lombok.Getter;

public class RequestVoteResultMsg {
    @Getter
    private final String nodeId;

    @Getter
    private final RequestVoteResult requestVoteResult;

    public RequestVoteResultMsg(String nodeId, RequestVoteResult requestVoteResult) {
        this.nodeId = nodeId;
        this.requestVoteResult = requestVoteResult;
    }

}
