package com.jzh.raft.core.model.rpc.message;

import com.jzh.raft.core.model.log.LogEntry;
import lombok.Getter;

import java.util.Collection;

public class AppendEntriesRpc {
    @Getter
    private final String leaderId;
    @Getter
    private final Long term;
    @Getter
    private final Collection<LogEntry> logEntries;
    @Getter
    private final Long prevLogIndex;
    @Getter
    private final Long prevLogTerm;
    @Getter
    private final Long commitIndex;

    public AppendEntriesRpc( String leaderId, Long term, Collection<LogEntry> logEntries, Long prevLogIndex, Long prevLogTerm, Long commitIndex) {
        this.leaderId = leaderId;
        this.term = term;
        this.logEntries = logEntries;
        this.prevLogIndex = prevLogIndex;
        this.prevLogTerm = prevLogTerm;
        this.commitIndex = commitIndex;
    }

}
