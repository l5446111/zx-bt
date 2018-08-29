package com.zx.bt.persistent.repository;

import com.zx.bt.persistent.entity.NodeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NodeIdRepository extends JpaRepository<NodeId, Long> {
    NodeId findByNodeId(String nodeId);
}
