package com.jzh.raft.core2.model.rpc;

import com.jzh.raft.core2.model.node.NodeAddress;
import com.jzh.raft.core2.model.rpc.msg.AppendEntriesResult;
import com.jzh.raft.core2.model.rpc.msg.AppendEntriesRpc;
import com.jzh.raft.core2.model.rpc.msg.RequestVoteResult;
import com.jzh.raft.core2.model.rpc.msg.RequestVoteRpc;

import java.util.Collection;

public interface IConnector {
    void sendRequestVoteRpc(Collection<NodeAddress> nodeAddresses, RequestVoteRpc requestVoteRpc);

    void replyRequestVoteResult(NodeAddress nodeAddress, RequestVoteResult requestVoteResult);

    void sendAppendEntriesRpc(Collection<NodeAddress> nodeAddresses, AppendEntriesRpc appendEntriesRpc);

    void replyAppendEntriesResult(NodeAddress nodeAddress, AppendEntriesResult appendEntriesResult);
}
