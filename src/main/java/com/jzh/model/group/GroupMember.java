package com.jzh.model.group;

import com.jzh.model.node.NodeEndPoint;
import lombok.Getter;

public class GroupMember {
    @Getter
    private final NodeEndPoint nodeEndPoint;

    public static GroupMember GetInstance(NodeEndPoint nodeEndPoint) {
        return new GroupMember(nodeEndPoint);
    }

    private GroupMember(NodeEndPoint nodeEndPoint) {
        this.nodeEndPoint = nodeEndPoint;
    }
}
