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
        nodeRole = new FollowerNodeRole(nodeStore.getCurrentTerm(), null, nodeStore.getVoteFor(), generateElectionTimeoutEvent());
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

    /**
     * 选举超时事件
     */
    private void electionTimeoutEvent() {
        this.nodeContext.getTaskExecutor().submit(this::processElectionTimeout);
    }

    /**
     * 处理选举超时事件
     */
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
        RequestVoteRpcMsg requestVoteRpcMsg = new RequestVoteRpcMsg(this.nodeContext.getSelfId(), requestVoteRpc);
        this.nodeContext.getConnector().sendRequestVote(requestVoteRpcMsg, this.nodeContext.getNodeGroup().listNodeEndPointExceptSelf());
    }

    /**
     * 日志复制事件
     */
    private void logReplicationEvent() {
        this.nodeContext.getTaskExecutor().submit(this::processLogReplicationEvent);
    }

    /**
     * 处理日志复制事件
     */
    private void processLogReplicationEvent() {
        for (GroupMember member : this.nodeContext.getNodeGroup().listMembersExceptSelf()) {
            replicateLogToMember(member);
        }
    }

    /**
     * 复制日志给某个具体member
     */
    private void replicateLogToMember(GroupMember member) {
        AppendEntriesRpc rpc = new AppendEntriesRpc(this.nodeContext.getSelfId(), this.nodeRole.getTerm(),
                Collections.emptyList(), 0L, 0L, 0L);
        AppendEntriesRpcMsg message = new AppendEntriesRpcMsg(this.nodeContext.getSelfId(), rpc);
        this.nodeContext.getConnector().sendAppendEntries(message, member.getNodeEndPoint());
    }

    /**
     * 收到请求选票请求
     */
    private void onReceiveRequestVoteRpc(RequestVoteRpcMsg message) {
        this.nodeContext.getTaskExecutor().submit(
                () -> {
                    NodeEndPoint replyNodeEndPoint = this.nodeContext.getNodeGroup().getMember(message.getCandidateId()).getNodeEndPoint();
                    RequestVoteResult result = processRequestVoteRpc(message);
                    RequestVoteResultMsg resultMsg = new RequestVoteResultMsg(this.nodeContext.getSelfId(), result);
                    this.nodeContext.getConnector().replyRequestVote(resultMsg, replyNodeEndPoint);
                }
        );
    }

    /**
     * 处理请求选票请求
     */
    private RequestVoteResult processRequestVoteRpc(RequestVoteRpcMsg message) {
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

    /**
     * 收到请求选票回复
     */
    private void onReceiveAppendEntriesResult(AppendEntriesResultMsg message) {
        this.nodeContext.getTaskExecutor().submit(() -> processReceiveAppendEntriesResult(message));
    }

    /**
     * 处理请求选票回复
     */
    private void processReceiveAppendEntriesResult(AppendEntriesResultMsg message) {
        if (NodeRoleEnum.LEADER.equals(this.nodeRole.getNodeRole())) {
            return;
        }
        if (message.getAppendEntriesResult().getTerm() > this.nodeRole.getTerm()) {
            roleChangeTo(new FollowerNodeRole(message.getAppendEntriesResult().getTerm(), null, null, generateElectionTimeoutEvent()));
        }
    }

    /**
     * 收到同步日志请求
     */
    private void onReceiveAppendEntriesRpc(AppendEntriesRpcMsg message) {
        this.nodeContext.getTaskExecutor().submit(() -> {
            NodeEndPoint leaderPoint = this.nodeContext.getNodeGroup().getMember(message.getLeaderId()).getNodeEndPoint();
            AppendEntriesResult result = processAppendEntriesRpc(message);
            AppendEntriesResultMsg resultMsg = new AppendEntriesResultMsg(this.nodeContext.getSelfId(), result);
            this.nodeContext.getConnector().replyAppendEntries(resultMsg, leaderPoint);

        });
    }

    /**
     * 处理同步日志请求
     */
    private AppendEntriesResult processAppendEntriesRpc(AppendEntriesRpcMsg message) {
        AppendEntriesRpc appendEntriesRpc = message.getAppendEntriesRpc();

        if (appendEntriesRpc.getTerm() < this.nodeRole.getTerm()) {
            return new AppendEntriesResult(false, this.nodeRole.getTerm());
        }

        // become follower or refresh election schedule
        if (appendEntriesRpc.getTerm() > this.nodeRole.getTerm()) {
            roleChangeTo(new FollowerNodeRole(appendEntriesRpc.getTerm(), appendEntriesRpc.getLeaderId(), null, generateElectionTimeoutEvent()));
            return new AppendEntriesResult(appendEntries(appendEntriesRpc), this.nodeRole.getTerm());
        }

        switch (this.nodeRole.getNodeRole()) {
            case LEADER:
                // theoretically impossible
                return new AppendEntriesResult(false, this.nodeRole.getTerm());
            case CANDIDATE:
                // more than one candidate, become a follower
                roleChangeTo(new FollowerNodeRole(appendEntriesRpc.getTerm(), appendEntriesRpc.getLeaderId(), null, generateElectionTimeoutEvent()));
                return new AppendEntriesResult(appendEntries(appendEntriesRpc), this.nodeRole.getTerm());
            case FOLLOWER:
                roleChangeTo(new FollowerNodeRole(appendEntriesRpc.getTerm(), appendEntriesRpc.getLeaderId(),
                        ((FollowerNodeRole)this.nodeRole).getVotedFor(), generateElectionTimeoutEvent()));
                return new AppendEntriesResult(appendEntries(appendEntriesRpc), this.nodeRole.getTerm());
            default:
                throw new IllegalArgumentException("unexpected node role, " + this.nodeRole.getNodeRole());
        }

    }

    /**
     * 同步日志
     */
    private Boolean appendEntries(AppendEntriesRpc appendEntriesRpc) {
        // todo 待实现
        return true;
    }

    /**
     * 收到同步日志回复
     */
    private void onReceiveRequestVoteResult(RequestVoteResultMsg message) {
        this.nodeContext.getTaskExecutor().submit(() -> processRequestVoteResult(message));
    }

    /**
     * 处理同步日志回复
     */
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

    /**
     * 生成并注册一个选举超时schedule
     */
    private ElectionSchedule generateElectionTimeoutEvent() {
        return this.nodeContext.getScheduleManagement().generateElectionSchedule(this::electionTimeoutEvent);
    }

    /**
     * 生成并注册一个日志同步schedule
     */
    private LogReplicationSchedule generateLogReplicationEvent() {
        return this.nodeContext.getScheduleManagement().generateLogReplicationSchedule(this::logReplicationEvent);
    }

    /**
     * 改变角色
     */
    // todo 事务
    private void roleChangeTo(AbstractNodeRole newRole) {
        INodeStore nodeStore = nodeContext.getNodeStore();
        nodeStore.storeCurrentTerm(newRole.getTerm());
        if (NodeRoleEnum.FOLLOWER.equals(newRole.getNodeRole())) {
            nodeStore.storeVoteFor(((FollowerNodeRole)newRole).getLeaderId());
        }
        this.nodeRole = newRole;
    }
}
