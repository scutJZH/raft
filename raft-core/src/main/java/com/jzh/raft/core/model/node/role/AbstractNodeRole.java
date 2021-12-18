package com.jzh.raft.core.model.node.role;

import com.jzh.raft.core.model.node.NodeRoleEnum;
import lombok.Getter;

public abstract class AbstractNodeRole {
    @Getter
    private final NodeRoleEnum nodeRole;
    @Getter
    private final Long term;

    protected AbstractNodeRole(NodeRoleEnum nodeRole, Long term) {
        this.nodeRole = nodeRole;
        this.term = term;
    }

    public abstract void stopScheduleTask();
}
