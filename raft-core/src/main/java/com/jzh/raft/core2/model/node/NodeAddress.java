package com.jzh.raft.core2.model.node;

import lombok.Getter;

public class NodeAddress {
    @Getter
    private final String host;

    @Getter
    private final Integer ip;

    public NodeAddress(String host, Integer ip) {
        this.host = host;
        this.ip = ip;
    }
}
