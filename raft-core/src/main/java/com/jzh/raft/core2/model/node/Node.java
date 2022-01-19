package com.jzh.raft.core2.model.node;

import com.jzh.raft.core2.model.node.context.NodeContext;
import com.jzh.raft.core2.model.node.role.*;
import com.jzh.raft.core2.model.rpc.msg.RequestVoteResult;
import com.jzh.raft.core2.model.rpc.msg.RequestVoteRpc;
import com.jzh.raft.core2.model.schedule.ElectionTimeoutSchedule;
import com.jzh.raft.core2.model.schedule.LogReplicationSchedule;

import java.util.Objects;

public class Node implements INode {
    private String nodeId;
    private AbsNodeRole role;
    private NodeAddress address;
    private NodeContext nodeContext;

    @Override
    public void start() {
        this.role = new FollowerRole(0, null, getANewElectionTimeoutSchedule());
    }

    @Override
    public void stop() {

    }

    private void electionTimeoutEvent() {
        this.nodeContext.getExecutorPool().submit(this::processElectionTimeoutEvent);
    }

    /**
     * 执行选举超时事件
     * 1.增加自己的当前任期号并且转换到候选人
     * 2.向集群中的其他服务器节点发送请求投票的 RPCs 来给自己投票
     */
    private void processElectionTimeoutEvent() {
        this.role = new CandidateRole(this.role.getCurrentTerm() + 1, getANewElectionTimeoutSchedule());
        RequestVoteRpc requestVoteRpc = new RequestVoteRpc(nodeId, role.getCurrentTerm(),
                this.nodeContext.getLastCommitLogTerm(), this.nodeContext.getLastCommitLogIndex());
        this.nodeContext.getConnector().sendRequestVoteRpc(nodeContext.getNodeAddressMap().values(), requestVoteRpc);
    }

    /**
     * receive request vote rpc
     * @param requestVoteRpc params
     */
    private void onReceiveRequestVoteRpc(RequestVoteRpc requestVoteRpc) {
        this.nodeContext.getExecutorPool().submit(() -> {
            RequestVoteResult result = processReceiveRequestVoteRpc(requestVoteRpc);
            this.nodeContext.getConnector().replyRequestVoteResult(nodeContext.getNodeAddressMap().get(requestVoteRpc.getNodeId()), result);
        });
    }

    /**
     * handle request vote rpc
     * @param requestVoteRpc params
     * @return request vote result
     */
    private RequestVoteResult processReceiveRequestVoteRpc(RequestVoteRpc requestVoteRpc) {
        // when requestVote.term is bigger than currentTerm
        // change to follower、update term、vote as appropriate
        // if requestVote's committed log is newer than or equals to node's, vote to the requester
        // first compare the term number, then compare the index
        if (requestVoteRpc.getTerm() > this.role.getCurrentTerm()) {
            String voteFor = null;
            if (isCommittedLogNewerThanOwn(requestVoteRpc.getLastCommittedLogEntryTerm(), requestVoteRpc.getLastCommittedLastLogEntryIndex())) {
                voteFor = requestVoteRpc.getNodeId();
            }
            this.role = new FollowerRole(requestVoteRpc.getTerm(), voteFor, getANewElectionTimeoutSchedule());
            return new RequestVoteResult(this.nodeId, true, this.role.getCurrentTerm());
        }

        // when requestVote.term is equals to currentTerm
        // follower: vote as appropriate, when don't vote to any node && requestVote's committed log is newer or equals than node's
        // (follower get the requestVote of others but don't vote)
        // candidate: don't vote
        // leader: don't vote
        if (Objects.equals(requestVoteRpc.getTerm(), this.role.getCurrentTerm())) {
            switch (this.role.getRoleType()) {
                case FOLLOWER:
                    FollowerRole ownRole = (FollowerRole)this.role;
                    if ((ownRole.getVoteFor() == null || Objects.equals(ownRole.getVoteFor(), requestVoteRpc.getNodeId())) &&
                            isCommittedLogNewerThanOwn(requestVoteRpc.getLastCommittedLogEntryTerm(), requestVoteRpc.getLastCommittedLastLogEntryIndex())) {
                        this.role = new FollowerRole(requestVoteRpc.getTerm(), requestVoteRpc.getNodeId(), getANewElectionTimeoutSchedule());
                        return new RequestVoteResult(this.nodeId, true, this.role.getCurrentTerm());
                    }
                    return new RequestVoteResult(this.nodeId, false, this.role.getCurrentTerm());
                case CANDIDATE:
                case LEADER:
                    return new RequestVoteResult(this.nodeId, false, this.role.getCurrentTerm());
            }
        }

        return new RequestVoteResult(this.nodeId, false, this.role.getCurrentTerm());

    }

    /**
     * receive request vote result reply
     * @param requestVoteResult params
     */
    private void onReceiveRequestVoteResult(RequestVoteResult requestVoteResult) {
        this.nodeContext.getExecutorPool().submit(() -> processReceiveRequestVoteResult(requestVoteResult));
    }

    private void processReceiveRequestVoteResult(RequestVoteResult requestVoteResult) {
        if (!RoleTypeEnum.CANDIDATE.equals(this.role.getRoleType())) {
            return;
        }
        if (!requestVoteResult.getResult() && requestVoteResult.getOwnTerm() > this.role.getCurrentTerm()) {
            this.role = new FollowerRole(requestVoteResult.getOwnTerm(), null, getANewElectionTimeoutSchedule());
            return;
        }
        if (requestVoteResult.getResult()) {
            CandidateRole candidateRole = (CandidateRole) (this.role);
            int voteCount = candidateRole.getVoteCount() + 1;
            if (voteCount > (this.nodeContext.getNodeAddressMap().size() + 1) / 2) {
                this.role = new LeaderRole(this.role.getCurrentTerm(), getANewLogReplicationSchedule());
            } else {
                candidateRole.setVoteCount(voteCount);
            }
        }
    }

    private ElectionTimeoutSchedule getANewElectionTimeoutSchedule() {
        return nodeContext.getScheduleManagement().generateElectionTimeoutSchedule(this::electionTimeoutEvent);
    }

    private LogReplicationSchedule getANewLogReplicationSchedule() {
        return nodeContext.getScheduleManagement().generateLogReplicationSchedule(this::logReplicationEvent);
    }

    private void logReplicationEvent() {
        this.nodeContext.getExecutorPool().submit(this::processLogReplicationEvent);
    }

    private void processLogReplicationEvent() {

    }

    private Boolean isCommittedLogNewerThanOwn(Long lastCommittedLogTerm, Long lastCommittedLogIndex) {
        return lastCommittedLogTerm > this.nodeContext.getLastCommitLogTerm() ||
                (Objects.equals(lastCommittedLogTerm, this.nodeContext.getLastCommitLogTerm()) &&
                        lastCommittedLogIndex >= this.nodeContext.getLastCommitLogIndex());
    }



}
