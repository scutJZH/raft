package com.jzh.raft.core2.model.rpc;

import com.jzh.raft.core2.model.node.NodeAddress;
import com.jzh.raft.core2.model.rpc.msg.AppendEntriesRpc;
import com.jzh.raft.core2.model.rpc.msg.RequestVoteRpc;

import java.util.Collection;

public interface IConnector {
    void sendRequestVoteRpc(Collection<NodeAddress> nodeAddresses, RequestVoteRpc requestVoteRpc);

    void sendAppendEntriesRpc(Collection<NodeAddress> nodeAddresses, AppendEntriesRpc appendEntriesRpc);
}
