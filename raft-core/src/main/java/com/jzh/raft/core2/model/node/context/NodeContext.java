package com.jzh.raft.core2.model.node.context;

import com.jzh.raft.core2.model.node.*;
import com.jzh.raft.core2.model.rpc.IConnector;
import com.jzh.raft.core2.model.schedule.ScheduleManagement;
import com.jzh.raft.core2.model.store.IStore;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NodeContext {
    @Getter
    private final IConnector connector;

    @Getter
    private final ExecutorService executorPool = Executors.newSingleThreadExecutor();

    @Getter
    private final IStore store;

    @Getter
    private final GroupMember nodeInfo;

    @Getter
    private final NodeGroup nodeGroup;

    @Getter
    @Setter
    private ScheduleManagement scheduleManagement;

    @Getter
    @Setter
    private Long lastCommitLogIndex;

    @Getter
    @Setter
    private Integer lastCommitLogTerm;

    public NodeContext(@NonNull GroupMember nodeInfo, @NonNull NodeGroup nodeGroup, @NonNull IConnector connector, @NonNull IStore store) {
        this.nodeInfo = nodeInfo;
        this.nodeGroup = nodeGroup;
        this.connector = connector;
        this.store = store;
    }

    public void init() {
        this.connector.init();
        this.store.init();
    }
}
