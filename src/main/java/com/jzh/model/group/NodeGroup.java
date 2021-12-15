package com.jzh.model.group;

import com.jzh.model.node.NodeEndPoint;
import com.jzh.model.node.NodeId;
import lombok.Getter;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 集群成员实体
 */
public class NodeGroup {
    @Getter
    private final NodeEndPoint self;
    private Map<NodeId, GroupMember> memberMap;

    public static NodeGroup GetInstance(NodeEndPoint self, NodeEndPoint nodeEndPoint) {
        return new NodeGroup(self, Collections.singleton(nodeEndPoint));
    }

    public static NodeGroup GetInstance(NodeEndPoint self, Collection<NodeEndPoint> nodeEndPoints) {
        return new NodeGroup(self, nodeEndPoints);
    }

    private NodeGroup(NodeEndPoint self, Collection<NodeEndPoint> nodeEndPoints) {
        if (self == null) {
            throw new IllegalArgumentException("self nodeEndPoint is null");
        }
        this.self = self;
        this.memberMap = buildMemberMap(self, nodeEndPoints);
    }

    private Map<NodeId, GroupMember> buildMemberMap(NodeEndPoint self, Collection<NodeEndPoint> nodeEndPoints) {
        if (nodeEndPoints.isEmpty()) {
            throw new IllegalArgumentException("nodeEndPoints is empty");
        }
        memberMap = new HashMap<>();
        checkMemberLegal(memberMap, nodeEndPoints);
        memberMap.put(self.getNodeId(), GroupMember.GetInstance(self));
        for (NodeEndPoint nodeEndPoint : nodeEndPoints) {
            memberMap.put(nodeEndPoint.getNodeId(), GroupMember.GetInstance(nodeEndPoint));
        }

        return memberMap;
    }

    /**
     * 检查member是否重复或者为self
     * @param memberMap
     * @param newNodeEndPoints
     */
    private void checkMemberLegal(Map<NodeId, GroupMember> memberMap, Collection<NodeEndPoint> newNodeEndPoints) {
        List<String> dupNodeIds = newNodeEndPoints.stream().map(NodeEndPoint::getNodeId)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream().filter(nodeIdLongEntry -> nodeIdLongEntry.getValue() > 1)
                .map(Map.Entry::getKey).map(NodeId::toString).collect(Collectors.toList());
        
        if (!dupNodeIds.isEmpty()) {
            throw new IllegalArgumentException("duplication nodeEndPoints, nodeIds : " + dupNodeIds);
        }
        
        for (NodeEndPoint nodeEndPoint : newNodeEndPoints) {
            if (memberMap.containsKey(nodeEndPoint.getNodeId())) {
                throw new IllegalArgumentException("node already exists, nodeId : " + nodeEndPoint.getNodeId());
            }
            if (nodeEndPoint.getNodeId() == this.self.getNodeId()) {
                throw new IllegalArgumentException("nodeEndPoints contains self");
            }
        }
    }

    public GroupMember getMember(NodeId nodeId) {
        return this.memberMap.get(nodeId);
    }

    public List<GroupMember> listAllMembers() {
        return new ArrayList<>(this.memberMap.values());
    }

    public List<GroupMember> listOtherMembers() {
        return this.memberMap.values().stream()
                .filter(groupMember -> !groupMember.getNodeEndPoint().equals(this.self))
                .collect(Collectors.toList());
    }

    public void addMember(NodeEndPoint nodeEndPoint) {
        addMembers(Collections.singleton(nodeEndPoint));
    }

    public void addMembers(Collection<NodeEndPoint> nodeEndPoints) {
        checkMemberLegal(this.memberMap, nodeEndPoints);
        for (NodeEndPoint nodeEndPoint : nodeEndPoints) {
            this.memberMap.put(nodeEndPoint.getNodeId(), GroupMember.GetInstance(nodeEndPoint));
        }
    }

    public NodeEndPoint delMember(NodeId nodeId) {
        GroupMember member = this.memberMap.remove(nodeId);
        if (member != null) {
            return member.getNodeEndPoint();
        }
        return null;
    }
}
