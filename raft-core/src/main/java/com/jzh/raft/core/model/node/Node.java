package com.jzh.raft.core.model.node;

import com.jzh.raft.core.model.group.GroupMember;
import com.jzh.raft.core.model.node.context.NodeContext;
import com.jzh.raft.core.model.node.role.AbstractNodeRole;
import com.jzh.raft.core.model.node.role.CandidateNodeRole;
import com.jzh.raft.core.model.node.role.FollowerNodeRole;
import com.jzh.raft.core.model.node.role.LeaderNodeRole;
import com.jzh.raft.core.model.rpc.message.*;
import com.jzh.raft.core.model.schedule.ElectionSchedule;
import com.jzh.raft.core.model.schedule.LogReplicationSchedule;
import com.jzh.raft.core.store.INodeStore;

import java.util.Collections;
import java.util.Objects;

public class Node implements INode {
    private Boolean started;
    private AbstractNodeRole nodeRole;
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
        ElectionSchedule electionSchedule = nodeContext.getScheduleManagement().generateElectionSchedule(this::electionTimeoutEvent);
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
    private void roleChangeTo(AbstractNodeRole newRole) {
        INodeStore nodeStore = nodeContext.getNodeStore();
        nodeStore.storeCurrentTerm(newRole.getTerm());
        if (NodeRoleEnum.FOLLOWER.equals(newRole.getNodeRole())) {
            nodeStore.storeVoteFor(((FollowerNodeRole)newRole).getLeaderId());
        }
        this.nodeRole = newRole;
    }

    private void electionTimeoutEvent() {
        this.nodeContext.getTaskExecutor().submit(this::processElectionTimeout);
    }

    private void processElectionTimeout() {
        if (NodeRoleEnum.LEADER.equals(this.nodeRole.getNodeRole())) {
            return;
        }

        // stop current schedule task
        this.nodeRole.stopScheduleTask();

        // for follower, become a candidate
        // for candidate, restart an election
        ElectionSchedule electionSchedule = nodeContext.getScheduleManagement().generateElectionSchedule(this::electionTimeoutEvent);
        roleChangeTo(new CandidateNodeRole(this.nodeRole.getTerm() + 1, electionSchedule));

        // send requestVote rpc
        RequestVoteRpc requestVoteRpc = new RequestVoteRpc(this.nodeRole.getTerm(), this.nodeContext.getSelfId(),
                this.nodeContext.getLastCommitLogIndex(), this.nodeContext.getLastCommitLogTerm());
        this.nodeContext.getConnector().sendRequestVote(requestVoteRpc, this.nodeContext.getNodeGroup().listNodeEndPointExceptSelf());
    }

    private void logReplicationEvent() {
        this.nodeContext.getTaskExecutor().submit(this::processLogReplicationEvent);
    }

    private void processLogReplicationEvent() {
        for (GroupMember member : this.nodeContext.getNodeGroup().listMembersExceptSelf()) {
            replicateLogToMember(member);
        }
    }

    private void replicateLogToMember(GroupMember member) {
        AppendEntriesRpc rpc = new AppendEntriesRpc(this.nodeContext.getSelfId(), this.nodeRole.getTerm(),
                Collections.emptyList(), 0L, 0L, 0L);
        this.nodeContext.getConnector().sendAppendEntries(rpc, member.getNodeEndPoint());
    }


    private void onReceiveRequestVoteRpc(RequestVoteMsg message) {
        this.nodeContext.getTaskExecutor().submit(
                () -> {
                    NodeEndPoint replyNodeEndPoint = this.nodeContext.getNodeGroup().getMember(message.getCandidateId()).getNodeEndPoint();
                    RequestVoteResult requestVoteResult = processRequestVoteRpc(message);
                    this.nodeContext.getConnector().replyRequestVote(requestVoteResult, replyNodeEndPoint);
                }
        );
    }

    private RequestVoteResult processRequestVoteRpc(RequestVoteMsg message) {
        RequestVoteRpc requestVoteRpc = message.getRequestVoteRpc();
        Long currentTerm = this.nodeRole.getTerm();
        // 1. the term of request is smaller than currentTerm: return false
        if (requestVoteRpc.getTerm() < currentTerm) {
            return new RequestVoteResult(false, currentTerm);
        }

        // 2. the term of request is bigger than currentTerm: return success
        if (requestVoteRpc.getTerm() > currentTerm) {
            // compare the last committed log
            if (this.nodeContext.getLastCommitLogTerm() > requestVoteRpc.getLastCommitLogTerm()) {
                return new RequestVoteResult(false, currentTerm);
            } else if (this.nodeContext.getLastCommitLogTerm().equals(requestVoteRpc.getLastCommitLogTerm())
                    && this.nodeContext.getLastCommitLogIndex() > requestVoteRpc.getLastCommitLogIndex()) {
                return new RequestVoteResult(false, currentTerm);
            }
            // change to the follower role and refresh election event schedule
            ElectionSchedule electionSchedule = this.nodeContext.getScheduleManagement().generateElectionSchedule(this::electionTimeoutEvent);
            roleChangeTo(new FollowerNodeRole(requestVoteRpc.getTerm(), null, message.getCandidateId(), electionSchedule));
            return new RequestVoteResult(true, currentTerm);
        }

        // 3. the term of request is equal to currentTerm
        // candidate and leader:
        if (NodeRoleEnum.CANDIDATE.equals(this.nodeRole.getNodeRole()) || NodeRoleEnum.LEADER.equals(this.nodeRole.getNodeRole())) {
            return new RequestVoteResult(false, currentTerm);
        }
        // follower:
        if (((FollowerNodeRole)this.nodeRole).getVotedFor() == null
                || ((FollowerNodeRole)this.nodeRole).getVotedFor().equals(requestVoteRpc.getCandidateId())) {
            return new RequestVoteResult(true, currentTerm);
        }

        return new RequestVoteResult(false, currentTerm);
    }

    private void onReceiveRequestVoteResult(RequestVoteResultMsg message) {
        this.nodeContext.getTaskExecutor().submit(() -> processRequestVoteResult(message));
    }

    private void processRequestVoteResult(RequestVoteResultMsg message) {
        RequestVoteResult requestVoteResult = message.getRequestVoteResult();

        if (!NodeRoleEnum.CANDIDATE.equals(this.nodeRole.getNodeRole())) {
            return;
        }

        // result is success
        if (requestVoteResult.getResult()) {
            ((CandidateNodeRole)this.nodeRole).voteCountPlusOne(message.getNodeId());
            int voteCount = ((CandidateNodeRole)this.nodeRole).getVoteCount();
            if (voteCount > (this.nodeContext.getNodeGroup().listMembersExceptSelf().size() / 2)) {
                // become leader
                roleChangeTo(new LeaderNodeRole(this.nodeRole.getTerm(), generateLogReplicationEvent()));
            }
        }

        // result is false
        if (requestVoteResult.getCurrentTerm() > this.nodeRole.getTerm()) {
            roleChangeTo(new FollowerNodeRole(requestVoteResult.getCurrentTerm(), null, null, generateElectionTimeoutEvent()));
        }

    }

    private ElectionSchedule generateElectionTimeoutEvent() {
        return this.nodeContext.getScheduleManagement().generateElectionSchedule(this::electionTimeoutEvent);
    }

    private LogReplicationSchedule generateLogReplicationEvent() {
        return this.nodeContext.getScheduleManagement().generateLogReplicationSchedule(this::logReplicationEvent);
    }
}
