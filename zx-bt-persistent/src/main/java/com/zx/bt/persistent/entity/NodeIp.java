package com.zx.bt.persistent.entity;

import com.zx.bt.hashspider.entity.Node;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@Entity
@Table(name = "node_ip")
@DynamicUpdate
@DynamicInsert
@ToString(exclude = "infoHashId")
public class NodeIp implements Serializable {
    private static final long serialVersionUID = -4729226164792757403L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, optional = false)
    @JoinColumn(name = "node_id")
    private NodeId nodeId;
    @Column(name = "ip", nullable = false, length = 15)
    private String ip;
    @Column(name = "port", nullable = false)
    private Integer port;
    @Column(name = "last_contract_date")
    private Date lastContractDate;
    @Column(name = "contract_count")
    private Long contractCount;
    @Column(name = "create_time")
    private Date createTime;
    @Column(name = "last_update_time")
    private Date lastUpdateTime;

    public NodeIp(Node node) {
        Date now = new Date();
        this.ip = node.getIp();
        this.port = node.getPort();
        this.lastContractDate = now;
        this.contractCount = 1L;
        this.createTime = now;
        this.lastUpdateTime = now;
    }
}
