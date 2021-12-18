package com.jzh.raft.core.model.rpc;

import com.jzh.raft.core.model.log.LogEntry;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Builder
public class AppendEntriesRpc {
    @Getter
    private final String leaderId;
    @Getter
    private final String term;
    @Getter
    @Setter
    private Collection<LogEntry> logEntries;
    @Getter
    @Setter
    private Long prevLogIndex;
    @Getter
    @Setter
    private Long prevLogTerm;
    @Getter
    @Setter
    private Long commitIndex;

}
