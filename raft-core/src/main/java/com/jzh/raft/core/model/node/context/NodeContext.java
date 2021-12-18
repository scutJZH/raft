package com.jzh.raft.core.model.node.context;

import com.jzh.raft.core.model.group.NodeGroup;
import com.jzh.raft.core.model.rpc.IConnector;
import com.jzh.raft.core.model.schedule.IScheduleManagement;
import com.jzh.raft.core.store.INodeStore;
import lombok.Getter;
import lombok.Setter;

public class NodeContext implements INodeContext {
    @Getter
    private final String selfId;
    @Getter
    private final NodeGroup nodeGroup;
    @Getter
    private final IConnector connector;
    @Getter
    private final IScheduleManagement scheduleManagement;
    @Getter
    private final INodeStore nodeStore;

    @Getter
    @Setter
    private Long lastCommitLogIndex;

    @Getter
    @Setter
    private Long lastCommitLogTerm;

    public NodeContext(String selfId, NodeGroup nodeGroup, IConnector connector, IScheduleManagement scheduleManagement, INodeStore nodeStore) {
        this.selfId = selfId;
        this.nodeGroup = nodeGroup;
        this.connector = connector;
        this.scheduleManagement = scheduleManagement;
        this.nodeStore = nodeStore;
    }

    @Override
    public void init() {
        this.connector.init();
    }

    @Override
    public void stop() {
        this.connector.close();
    }
}
