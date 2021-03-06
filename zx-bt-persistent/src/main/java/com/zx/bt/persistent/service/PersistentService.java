package com.zx.bt.persistent.service;

import com.zx.bt.common.entity.InfoHash;
import com.zx.bt.hashspider.entity.Node;
import com.zx.bt.persistent.entity.InfoHashId;
import com.zx.bt.persistent.entity.InfoHashIp;
import com.zx.bt.persistent.entity.NodeId;
import com.zx.bt.persistent.entity.NodeIp;
import com.zx.bt.persistent.repository.InfoHashIdRespository;
import com.zx.bt.persistent.repository.InfoHashIpRespository;
import com.zx.bt.persistent.repository.NodeIdRepository;
import com.zx.bt.persistent.repository.NodeIpRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;

@Service
@Slf4j
public class PersistentService {
    @Autowired
    private NodeIpRepository nodeIpRepository;
    @Autowired
    private NodeIdRepository nodeIdRepository;
    @Autowired
    private InfoHashIdRespository infoHashIdRespository;
    @Autowired
    private InfoHashIpRespository infoHashIpRespository;

    public void persistent(Object obj) {
        if (obj instanceof Node) {
            persistentNode((Node) obj);
        } else if (obj instanceof InfoHash) {
            persistentInfoHash((InfoHash) obj);
        } else {
            log.warn("未知对象;{}，不能持久化内容:{}", obj.getClass(), obj);
        }
    }

    @Transactional
    public void persistentNode(Node node) {
        NodeId nodeId = nodeIdRepository.findByNodeId(node.getNodeId());
        if (nodeId == null) {
            nodeId = new NodeId(node);
        } else {
            Date now = new Date();
            nodeId.setContractCount(nodeId.getContractCount() + 1);
            nodeId.setLastUpdateTime(now);
            nodeId.setLastContractDate(now);
        }
//            log.info("{}nodeId:{}", nodeId.getId() == null ? "新增" : "更新", nodeId);

        try {
            nodeIdRepository.save(nodeId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        NodeIp nodeIp = nodeIpRepository.findByNodeIdAndIpAndPort(nodeId.getId(), node.getIp(), node.getPort());
        if (nodeIp == null) {
            nodeIp = new NodeIp(node);
        } else {
            nodeIp.setContractCount(nodeIp.getContractCount() + 1);
            nodeIp.setLastContractDate(new Date());
            nodeIp.setLastUpdateTime(new Date());
        }
        nodeIp.setNodeId(nodeId.getId());
//            log.info("{}nodeIp:{}", nodeIp.getId() == null ? "新增" : "更新", nodeIp);
        try {
            nodeIpRepository.save(nodeIp);
        } catch (Exception e) {
            log.error("", e);
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void persistentInfoHash(InfoHash infoHash) {
        if (StringUtils.isBlank(infoHash.getInfoHash())) {
            log.warn("infoHash 对象infoHash不能为空:{}", infoHash);
            return;
        }

        InfoHashId infoHashId = infoHashIdRespository.findByInfoHash(infoHash.getInfoHash());
        if (infoHashId == null) {
            infoHashId = new InfoHashId(infoHash);
        } else {
            infoHashId.setContractCount(infoHashId.getContractCount() + 1);
            infoHashId.setLastContractDate(new Date());
            infoHashId.setLastUpdateTime(new Date());
        }
//            log.info("{}infoHashId:{}", infoHashId.getId() == null ? "新增" : "更新", infoHashId);
        try {
            infoHashIdRespository.save(infoHashId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String peerAddress = infoHash.getPeerAddress().replaceAll(";", "");
        String[] peerAddressArr = peerAddress.split(":");
        String ip = peerAddressArr[0];
        Integer port = Integer.valueOf(peerAddressArr[1]);
        InfoHashIp infoHashIp = infoHashIpRespository.findByInfoHashIdAndIpAndPort(infoHashId.getId(), ip, port);
        if (infoHashIp == null) {
            infoHashIp = new InfoHashIp(infoHash);
        } else {
            infoHashIp.setContractCount(infoHashIp.getContractCount() + 1);
            infoHashIp.setLastContractDate(new Date());
            infoHashIp.setLastUpdateTime(new Date());
        }
        infoHashIp.setInfoHashId(infoHashId.getId());
//            log.info("{}infoHashIp:{}", infoHashIp.getId() == null ? "新增" : "更新", infoHashIp);
        try {
            infoHashIpRespository.save(infoHashIp);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
