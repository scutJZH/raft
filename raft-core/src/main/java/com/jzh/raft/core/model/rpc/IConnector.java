package com.jzh.raft.core.model.rpc;

import com.jzh.raft.core.model.node.NodeEndPoint;

import java.util.Collection;

public interface IConnector {
    void init();

    void sendRequestVote(RequestVoteRpc req, Collection<NodeEndPoint> nodeEndPoints);

    void replyRequestVote(RequestVoteResult resp, NodeEndPoint nodeEndPoint);

    void sendAppendEntries(AppendEntriesRpc req, Collection<NodeEndPoint> nodeEndPoints);

    void replyAppendEntries(AppendEntriesResult resp, NodeEndPoint nodeEndPoint);

    void close();
}
