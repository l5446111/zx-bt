package com.zx.bt.persistent.entity;


import com.zx.bt.common.entity.InfoHash;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * author:ZhengXing
 * datetime:2018-02-15 19:43
 * 存储info_hash信息
 */
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Data
@Entity
@Table(name = "info_hash_id")
@DynamicUpdate
@DynamicInsert
public class InfoHashId implements Serializable {

    private static final long serialVersionUID = 3558173996998467618L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * infoHash信息,16进制形式
     */
    @Column(name = "info_hash", length = 40)
    private String infoHash;
    @Column(name = "last_contract_date")
    private Date lastContractDate;
    @Column(name = "contract_count")
    private Long contractCount;
    @Column(name = "create_time")
    private Date createTime;
    @Column(name = "last_update_time")
    private Date lastUpdateTime;

    public InfoHashId(InfoHash infoHash) {
        this.infoHash = infoHash.getInfoHash();
        Date now = new Date();
        this.lastContractDate = now;
        this.contractCount = 1L;
        this.createTime = now;
        this.lastUpdateTime = now;
    }
}
