package com.jzh.raft.core.store;

public interface INodeStore {
    Long getCurrentTerm();

    String getVoteFor();

    void storeCurrentTerm(Long currentTerm);

    String storeVoteFor(String nodeId);

}
