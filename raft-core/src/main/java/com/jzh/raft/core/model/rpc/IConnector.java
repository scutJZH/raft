package com.jzh.raft.core.model.rpc;

import com.jzh.raft.core.model.node.NodeEndPoint;
import com.jzh.raft.core.model.rpc.message.AppendEntriesResult;
import com.jzh.raft.core.model.rpc.message.AppendEntriesRpc;
import com.jzh.raft.core.model.rpc.message.RequestVoteResult;
import com.jzh.raft.core.model.rpc.message.RequestVoteRpc;

import java.util.Collection;

public interface IConnector {
    void init();

    void sendRequestVote(RequestVoteRpc req, Collection<NodeEndPoint> nodeEndPoints);

    void replyRequestVote(RequestVoteResult resp, NodeEndPoint nodeEndPoint);

    void sendAppendEntries(AppendEntriesRpc req, NodeEndPoint nodeEndPoints);

    void replyAppendEntries(AppendEntriesResult resp, NodeEndPoint nodeEndPoint);

    void close();
}
