package com.zx.bt.persistent.entity;

import com.zx.bt.hashspider.entity.Node;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity
@Table(name = "node_id")
@DynamicUpdate
@DynamicInsert
public class NodeId implements Serializable {
    private static final long serialVersionUID = 4782965473434084747L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "node_id", length = 40)
    private String nodeId;

    @Column(name = "last_contract_date")
    private Date lastContractDate;
    @Column(name = "contract_count")
    private Long contractCount;
    @Column(name = "create_time")
    private Date createTime;
    @Column(name = "last_update_time")
    private Date lastUpdateTime;

    public NodeId(Node node) {
        Date now = new Date();
        this.nodeId = node.getNodeId();
        this.lastContractDate = now;
        this.contractCount = 1L;
        this.createTime = now;
        this.lastContractDate = now;
        this.lastUpdateTime = now;
    }

}
