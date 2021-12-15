package com.jzh.model.node;

/**
 * 节点Id实体类
 */
public class NodeId {
    private final String nodeId;

    public NodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public static NodeId GetInstance(String id) {
        return new NodeId(id);
    }

    public String getNodeId() {
        return nodeId;
    }
}
