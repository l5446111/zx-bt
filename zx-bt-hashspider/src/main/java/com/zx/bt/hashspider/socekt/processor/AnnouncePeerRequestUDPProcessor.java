package com.zx.bt.hashspider.socekt.processor;

import com.zx.bt.common.entity.InfoHash;
import com.zx.bt.common.util.CodeUtil;
import com.zx.bt.hashspider.dto.bt.AnnouncePeer;
import com.zx.bt.hashspider.entity.Node;
import com.zx.bt.hashspider.enums.MethodEnum;
import com.zx.bt.hashspider.enums.NodeRankEnum;
import com.zx.bt.hashspider.enums.YEnum;
import com.zx.bt.hashspider.service.QueueService;
import com.zx.bt.hashspider.socekt.Sender;
import com.zx.bt.hashspider.store.RoutingTable;
import com.zx.bt.hashspider.task.FindNodeTask;
import com.zx.bt.hashspider.util.BTUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * author:ZhengXing
 * datetime:2018/3/1 0001 10:30
 * ANNOUNCE_PEER 请求 处理器
 */
@Order(2)
@Slf4j
@Component
public class AnnouncePeerRequestUDPProcessor extends UDPProcessor {
    private static final String LOG = "[ANNOUNCE_PEER]";

    private final List<RoutingTable> routingTables;
    //private final GetPeersTask getPeersTask;
    private final Sender sender;
    //private final InfoHashService infoHashService;
    private final FindNodeTask findNodeTask;
    @Autowired
    private QueueService queueService;


    public AnnouncePeerRequestUDPProcessor(List<RoutingTable> routingTables, Sender sender, FindNodeTask findNodeTask
    ) {
        this.routingTables = routingTables;
        this.sender = sender;
        this.findNodeTask = findNodeTask;
    }

    @Override
    boolean process1(ProcessObject processObject) {
        AnnouncePeer.RequestContent requestContent = new AnnouncePeer.RequestContent(processObject.getRawMap(), processObject.getSender().getPort());
        log.info("{}收到消息.", LOG);
        //入库
        queueService.pushInfoHash(new InfoHash(requestContent.getInfo_hash(), BTUtil.getIpBySender(processObject.getSender()) + ":" + requestContent.getPort() + ";"));
        //infoHashService.saveInfoHash(requestContent.getInfo_hash(), BTUtil.getIpBySender(processObject.getSender()) + ":" + requestContent.getPort() + ";");
        //尝试从get_peers等待任务队列删除该任务,正在进行的任务可以不删除..因为删除比较麻烦.要遍历value
        //getPeersTask.remove(requestContent.getInfo_hash());
        //回复
        this.sender.announcePeerReceive(processObject.getMessageInfo().getMessageId(), processObject.getSender(), nodeIds.get(processObject.getIndex()), processObject.getIndex());
        Node node = new Node(CodeUtil.hexStr2Bytes(requestContent.getId()), processObject.getSender(), NodeRankEnum.ANNOUNCE_PEER.getCode());
        //加入路由表
        routingTables.get(processObject.getIndex()).put(node);
        //入库
        queueService.pushInfoHash(node);
        //加入findNode任务队列
        findNodeTask.put(processObject.getSender());
        return true;
    }

    @Override
    boolean isProcess(ProcessObject processObject) {
        return MethodEnum.ANNOUNCE_PEER.equals(processObject.getMessageInfo().getMethod()) && YEnum.QUERY.equals(processObject.getMessageInfo().getStatus());
    }
}
