package com.jzh.raft.core2.model.node;

import lombok.Getter;
import lombok.NonNull;

import java.util.Objects;

public class NodeId {
    @Getter
    private final String id;

    public NodeId(@NonNull String id) {
        this.id = id;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof NodeId && id.equals(((NodeId) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
