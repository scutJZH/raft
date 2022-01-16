package com.jzh.raft.core2.model.node.role;

import lombok.Getter;

public abstract class AbsNodeRole {
    @Getter
    private final Integer currentTerm;

    @Getter
    private final RoleTypeEnum roleType;

    public abstract void stopScheduleTask();

    protected AbsNodeRole(Integer currentTerm, RoleTypeEnum roleType) {
        this.currentTerm = currentTerm;
        this.roleType = roleType;
    }
}
