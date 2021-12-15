package com.jzh.model.node;

/**
 * 节点地址实体类
 */
public class NodeAddress {
    private final String host;
    private final Integer port;

    public NodeAddress(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }
}
