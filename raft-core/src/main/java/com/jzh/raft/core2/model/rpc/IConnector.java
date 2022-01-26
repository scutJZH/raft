package com.jzh.raft.core2.model.rpc;

import com.jzh.raft.core2.model.rpc.msg.AppendEntriesResult;
import com.jzh.raft.core2.model.rpc.msg.AppendEntriesRpc;
import com.jzh.raft.core2.model.rpc.msg.RequestVoteResult;
import com.jzh.raft.core2.model.rpc.msg.RequestVoteRpc;

import java.util.Collection;

public interface IConnector {
    void init();

    void sendRequestVoteRpc(Collection<String> nodeAddresses, RequestVoteRpc requestVoteRpc);

    void replyRequestVoteResult(String nodeAddress, RequestVoteResult requestVoteResult);

    void sendAppendEntriesRpc(Collection<String> nodeAddresses, AppendEntriesRpc appendEntriesRpc);

    void replyAppendEntriesResult(String nodeAddress, AppendEntriesResult appendEntriesResult);
}
