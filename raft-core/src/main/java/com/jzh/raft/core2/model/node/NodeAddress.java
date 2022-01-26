package com.jzh.raft.core2.model.node;

import lombok.Getter;
import lombok.NonNull;

public class NodeAddress {
    @Getter
    private final String host;

    @Getter
    private final Integer ip;

    public NodeAddress(@NonNull String host, @NonNull Integer ip) {
        this.host = host;
        this.ip = ip;
    }

    public String getAddress() {
        return host + ":" + ip;
    }
}
