package com.zx.bt.persistent.repository;

import com.zx.bt.persistent.entity.InfoHashId;
import com.zx.bt.persistent.entity.InfoHashIp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InfoHashIpRespository extends JpaRepository<InfoHashIp, Long> {
    InfoHashIp findByInfoHashIdAndIpAndPort(Long infoHashId, String ip, Integer port);
}
