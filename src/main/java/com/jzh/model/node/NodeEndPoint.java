package com.jzh.model.node;

import lombok.Getter;

/**
 * 节点实体类
 */
public class NodeEndPoint {
    @Getter
    private final NodeId nodeId;
    @Getter
    private final NodeAddress address;

    public static NodeEndPoint GetInstance(NodeId nodeId, NodeAddress nodeAddress) {
        return new NodeEndPoint(nodeId, nodeAddress);
    }

    public static NodeEndPoint GetInstance(NodeId nodeId, String host, Integer port) {
        return new NodeEndPoint(nodeId, new NodeAddress(host, port));
    }

    private NodeEndPoint(NodeId nodeId, NodeAddress nodeAddress) {
        this.nodeId = nodeId;
        this.address = nodeAddress;
    }

}
