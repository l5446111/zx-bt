package com.zx.bt.persistent.repository;

import com.zx.bt.persistent.entity.InfoHashId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InfoHashIdRespository extends JpaRepository<InfoHashId, Long> {
    InfoHashId findByInfoHash(String infoHash);
}
