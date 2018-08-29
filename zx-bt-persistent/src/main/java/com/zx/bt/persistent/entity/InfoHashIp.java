package com.zx.bt.persistent.entity;


import com.zx.bt.common.entity.InfoHash;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * author:ZhengXing
 * datetime:2018-02-15 19:43
 * 存储info_hash信息
 */
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Data
@ToString(exclude = "infoHashId")
@Entity
@Table(name = "info_hash_ip")
@DynamicUpdate
@DynamicInsert
public class InfoHashIp implements Serializable {

    private static final long serialVersionUID = 3558173996998467618L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "ip")
    private String ip;
    @Column(name = "port")
    private Integer port;
    @Column(name = "last_contract_date")
    private Date lastContractDate;
    @Column(name = "contract_count")
    private Long contractCount;
    @Column(name = "create_time")
    private Date createTime;
    @Column(name = "last_update_time")
    private Date lastUpdateTime;
    @Column(name = "info_hash_id")
    private Long infoHashId;

    public InfoHashIp(InfoHash infoHash) {
        String peerAddress = infoHash.getPeerAddress().replaceAll(";", "");
        String[] peerAddressArr = peerAddress.split(":");
        String ip = peerAddressArr[0];
        Integer port = Integer.valueOf(peerAddressArr[1]);
        this.ip = ip;
        this.port = port;
        Date now = new Date();
        this.lastContractDate = now;
        this.createTime = now;
        this.lastUpdateTime = now;
        this.contractCount = 1L;
    }

}
