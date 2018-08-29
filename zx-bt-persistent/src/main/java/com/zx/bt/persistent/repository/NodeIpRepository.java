package com.zx.bt.persistent.repository;

import com.zx.bt.persistent.entity.NodeId;
import com.zx.bt.persistent.entity.NodeIp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NodeIpRepository extends JpaRepository<NodeIp, Long> {
    NodeIp findByNodeIdAndIpAndPort(NodeId nodeId, String ip, Integer port);
}
