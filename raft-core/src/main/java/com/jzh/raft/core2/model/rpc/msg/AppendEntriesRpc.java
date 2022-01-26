package com.jzh.raft.core2.model.rpc.msg;


import com.jzh.raft.core2.model.log.LogEntry;
import lombok.Getter;

import java.util.List;

public class AppendEntriesRpc {
    @Getter
    private final Long commitIndex;
    @Getter
    private final Integer commitTerm;
    @Getter
    private final List<LogEntry> logEntries;

    public AppendEntriesRpc(Long commitIndex, Integer commitTerm, List<LogEntry> logEntries) {
        this.commitIndex = commitIndex;
        this.commitTerm = commitTerm;
        this.logEntries = logEntries;
    }
}
