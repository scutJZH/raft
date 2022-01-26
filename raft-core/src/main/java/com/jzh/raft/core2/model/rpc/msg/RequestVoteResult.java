package com.jzh.raft.core2.model.rpc.msg;

import com.jzh.raft.core2.model.node.NodeId;
import lombok.Getter;
import lombok.NonNull;

public class RequestVoteResult {
    @Getter
    private final NodeId nodeId;
    @Getter
    private final Boolean result;
    @Getter
    private final Integer ownTerm;

    public RequestVoteResult(@NonNull NodeId nodeId, @NonNull Boolean result, @NonNull Integer ownTerm) {
        this.nodeId = nodeId;
        this.result = result;
        this.ownTerm = ownTerm;
    }
}
