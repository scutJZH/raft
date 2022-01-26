package com.jzh.raft.core2.model.node;

import lombok.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NodeGroup {
    private final Map<NodeId, GroupMember> groupMemberMap;

    public NodeGroup() {
        this.groupMemberMap = new HashMap<>();
    }

    public List<String> getMemberAddresses() {
        return groupMemberMap.values().stream()
                .map(GroupMember::getNodeEndPoint)
                .map(NodeEndPoint::getAddress)
                .collect(Collectors.toList());
    }

    public GroupMember getMember(@NonNull NodeId nodeId) {
        return this.groupMemberMap.get(nodeId);
    }

    public void addMember(@NonNull GroupMember member) {
        groupMemberMap.put(member.getNodeEndPoint().getNodeId(), member);
    }

    public GroupMember removeMember(NodeId nodeId) {
        return groupMemberMap.remove(nodeId);
    }

    public Integer size() {
        return groupMemberMap.size();
    }


}
