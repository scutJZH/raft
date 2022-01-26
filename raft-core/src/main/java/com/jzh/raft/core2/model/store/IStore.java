package com.jzh.raft.core2.model.store;

public interface IStore {
    void init();

    void saveCurrentTerm(Integer currentTerm);

    Integer getCurrentTerm();
}
