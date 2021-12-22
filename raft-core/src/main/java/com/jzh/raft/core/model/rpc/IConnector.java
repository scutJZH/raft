package com.jzh.raft.core.model.rpc;

import com.jzh.raft.core.model.node.NodeEndPoint;
import com.jzh.raft.core.model.rpc.message.*;

import java.util.Collection;

public interface IConnector {
    void init();

    void sendRequestVote(RequestVoteRpcMsg req, Collection<NodeEndPoint> nodeEndPoints);

    void replyRequestVote(RequestVoteResultMsg resp, NodeEndPoint nodeEndPoint);

    void sendAppendEntries(AppendEntriesRpcMsg req, NodeEndPoint nodeEndPoints);

    void replyAppendEntries(AppendEntriesResultMsg resp, NodeEndPoint nodeEndPoint);

    void close();
}
