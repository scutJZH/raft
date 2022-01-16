package com.jzh.raft.core2.model.node.context;

import com.jzh.raft.core2.model.node.NodeAddress;
import com.jzh.raft.core2.model.rpc.IConnector;
import com.jzh.raft.core2.model.schedule.ScheduleManagement;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NodeContext {
    @Getter
    private final IConnector connector;

    @Getter
    private final ExecutorService executorPool = Executors.newSingleThreadExecutor();

    @Getter
    private final Map<String, NodeAddress> nodeAddressMap = new HashMap<>();

    @Getter
    @Setter
    private ScheduleManagement scheduleManagement;

    @Getter
    @Setter
    private Long lastCommitLogIndex;

    @Getter
    @Setter
    private Long lastCommitLogTerm;

    public NodeContext(IConnector connector) {
        this.connector = connector;
    }
}
