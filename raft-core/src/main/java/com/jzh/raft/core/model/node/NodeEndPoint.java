package com.jzh.raft.core.model.node;

import lombok.Getter;

/**
 * 节点实体类
 */
public class NodeEndPoint {
    @Getter
    private final String nodeId;
    @Getter
    private final NodeAddress address;

    public static NodeEndPoint GetInstance(String nodeId, NodeAddress nodeAddress) {
        return new NodeEndPoint(nodeId, nodeAddress);
    }

    public static NodeEndPoint GetInstance(String nodeId, String host, Integer port) {
        return new NodeEndPoint(nodeId, new NodeAddress(host, port));
    }

    private NodeEndPoint(String nodeId, NodeAddress nodeAddress) {
        this.nodeId = nodeId;
        this.address = nodeAddress;
    }

}
