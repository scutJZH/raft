package com.jzh.raft.core.model.node;

import com.jzh.raft.core.model.node.context.NodeContext;
import com.jzh.raft.core.model.node.role.AbstractNodeRole;
import com.jzh.raft.core.model.node.role.CandidateNodeRole;
import com.jzh.raft.core.model.node.role.FollowerNodeRole;
import com.jzh.raft.core.model.rpc.RequestVoteRpc;
import com.jzh.raft.core.model.schedule.ElectionSchedule;
import com.jzh.raft.core.store.INodeStore;
import lombok.Getter;

public class Node implements INode {
    private Boolean started;
    @Getter
    private AbstractNodeRole nodeRole;
    @Getter
    private final NodeContext nodeContext;

    public Node(NodeContext nodeContext) {
        this.nodeContext = nodeContext;
    }

    @Override
    public synchronized void start() {
        if (started) {
            return;
        }
        nodeContext.init();
        INodeStore nodeStore = nodeContext.getNodeStore();
        ElectionSchedule electionSchedule = nodeContext.getScheduleManagement().generateElectionSchedule(this::doElectionTask);
        nodeRole = new FollowerNodeRole(nodeStore.getCurrentTerm(), null, nodeStore.getVoteFor(), electionSchedule);
        started = true;
    }

    @Override
    public synchronized void stop() {
        if (!started) {
            return;
        }
        nodeContext.stop();
        started = false;
    }

    // todo 事务
    private void ruleChangeTo(AbstractNodeRole newRole) {
        INodeStore nodeStore = nodeContext.getNodeStore();
        nodeStore.storeCurrentTerm(newRole.getTerm());
        if (newRole.getNodeRole() == NodeRoleEnum.FOLLOWER) {
            nodeStore.storeVoteFor(((FollowerNodeRole)newRole).getLeaderId());
        }
        this.nodeRole = newRole;
    }

    private void doElectionTask() {
        if (this.nodeRole.getNodeRole() == NodeRoleEnum.LEADER) {
            return;
        }

        // stop current schedule task
        this.nodeRole.stopScheduleTask();

        // for follower, become a candidate
        // for candidate, restart an election
        ElectionSchedule electionSchedule = nodeContext.getScheduleManagement().generateElectionSchedule(this::doElectionTask);
        ruleChangeTo(new CandidateNodeRole(this.nodeRole.getTerm() + 1, electionSchedule));

        // send requestVote rpc
        RequestVoteRpc requestVoteRpc = new RequestVoteRpc(this.nodeRole.getTerm(), this.nodeContext.getSelfId(), this.nodeContext.getLastCommitLogIndex(), this.nodeContext.getLastCommitLogTerm());
        this.nodeContext.getConnector().sendRequestVote(requestVoteRpc, this.nodeContext.getNodeGroup().listNodeEndPointExceptSelf());
    }

    private void doAppendEntriesTask() {

    }
}
