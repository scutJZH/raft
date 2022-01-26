package com.jzh.raft.core2.model.node;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public class NodeEndPoint {
    @Getter
    private final NodeId nodeId;

    @Setter
    private NodeAddress nodeAddress;

    public NodeEndPoint(@NonNull NodeId nodeId, @NonNull NodeAddress nodeAddress) {
        this.nodeId = nodeId;
        this.nodeAddress = nodeAddress;
    }

    public String getAddress() {
        return this.nodeAddress.getAddress();
    }
}
