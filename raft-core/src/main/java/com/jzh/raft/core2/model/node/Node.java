package com.jzh.raft.core2.model.node;

import com.jzh.raft.core2.model.node.context.NodeContext;
import com.jzh.raft.core2.model.node.role.AbsNodeRole;
import com.jzh.raft.core2.model.node.role.CandidateRole;
import com.jzh.raft.core2.model.node.role.FollowerRole;
import com.jzh.raft.core2.model.rpc.msg.RequestVoteRpc;
import com.jzh.raft.core2.model.schedule.ElectionTimeoutSchedule;

public class Node {
    private String nodeId;
    private AbsNodeRole role;
    private NodeAddress address;
    private NodeContext nodeContext;

    public void start() {
        this.role = new FollowerRole(0, null, getANewElectionTimeoutSchedule());
    }

    private void electionTimeoutEvent() {
        // 1.增加自己的当前任期号并且转换到候选人
        // 2.向集群中的其他服务器节点发送请求投票的 RPCs 来给自己投票
        this.role = new CandidateRole(this.role.getCurrentTerm() + 1, getANewElectionTimeoutSchedule());
        RequestVoteRpc requestVoteRpc = new RequestVoteRpc(nodeId, role.getCurrentTerm(),
                this.nodeContext.getLastCommitLogTerm(), this.nodeContext.getLastCommitLogIndex());
        this.nodeContext.getConnector().sendRequestVoteRpc(nodeContext.getNodeAddressMap().values(), requestVoteRpc);
    }

    private void processElectionTimeoutEvent() {
        this.nodeContext.getExecutorPool().submit(this::electionTimeoutEvent);
    }

    private ElectionTimeoutSchedule getANewElectionTimeoutSchedule() {
        return nodeContext.getScheduleManagement().generateElectionTimeoutSchedule(this::processElectionTimeoutEvent);
    }



}
