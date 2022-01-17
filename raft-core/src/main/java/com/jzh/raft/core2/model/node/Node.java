package com.jzh.raft.core2.model.node;

import com.jzh.raft.core2.model.node.context.NodeContext;
import com.jzh.raft.core2.model.node.role.AbsNodeRole;
import com.jzh.raft.core2.model.node.role.CandidateRole;
import com.jzh.raft.core2.model.node.role.FollowerRole;
import com.jzh.raft.core2.model.rpc.msg.RequestVoteResult;
import com.jzh.raft.core2.model.rpc.msg.RequestVoteRpc;
import com.jzh.raft.core2.model.schedule.ElectionTimeoutSchedule;

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

    private ElectionTimeoutSchedule getANewElectionTimeoutSchedule() {
        return nodeContext.getScheduleManagement().generateElectionTimeoutSchedule(this::electionTimeoutEvent);
    }

    private void onReceiveRequestVoteRpc(RequestVoteRpc requestVoteRpc) {
        this.nodeContext.getExecutorPool().submit(() -> {
            RequestVoteResult result = processReceiveRequestVoteRpc(requestVoteRpc);
            this.nodeContext.getConnector().replyRequestVoteResult(nodeContext.getNodeAddressMap().get(requestVoteRpc.getNodeId()), result);
        });
    }

    /**
     *
     * @param requestVoteRpc
     * @return
     */
    private RequestVoteResult processReceiveRequestVoteRpc(RequestVoteRpc requestVoteRpc) {

    }



}
