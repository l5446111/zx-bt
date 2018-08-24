package com.zx.bt.hashspider.socekt.processor;

import com.zx.bt.common.util.CodeUtil;
import com.zx.bt.hashspider.entity.Node;
import com.zx.bt.hashspider.enums.MethodEnum;
import com.zx.bt.hashspider.enums.NodeRankEnum;
import com.zx.bt.hashspider.enums.YEnum;
import com.zx.bt.hashspider.socekt.Sender;
import com.zx.bt.hashspider.store.RoutingTable;
import com.zx.bt.hashspider.util.BTUtil;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;


/**
 * author:ZhengXing
 * datetime:2018/3/1 0001 10:30
 * ANNOUNCE_PEER 请求 处理器
 */
@Order(3)
@Slf4j
@Component
public class GetPeersRequestUDPProcessor extends UDPProcessor{
	private static final String LOG = "[GET_PEERS]";

	private final List<RoutingTable> routingTables;
	private final Sender sender;

	public GetPeersRequestUDPProcessor(List<RoutingTable> routingTables, Sender sender) {
		this.routingTables = routingTables;
		this.sender = sender;
	}

	@Override
	boolean process1(ProcessObject processObject) {
			Map<String, Object> rawMap = processObject.getRawMap();
			InetSocketAddress sender = processObject.getSender();
			int index = processObject.getIndex();

			Map<String, Object> aMap = BTUtil.getParamMap(rawMap, "a", "GET_PEERS,找不到a参数.map:" + rawMap);
			byte[] infoHash = BTUtil.getParamString(aMap, "info_hash", "GET_PEERS,找不到info_hash参数.map:" + rawMap).getBytes(CharsetUtil.ISO_8859_1);
			byte[] id = BTUtil.getParamString(aMap, "id", "GET_PEERS,找不到id参数.map:" + rawMap).getBytes(CharsetUtil.ISO_8859_1);
			List<Node> nodes = routingTables.get(index).getForTop8(infoHash);
//                    log.info("{}GET_PEERS,发送者:{},info_hash:{}", LOG, sender,info_hash);
			//回复时,将自己的nodeId伪造为 和该节点异或值相差不大的值
			this.sender.getPeersReceive(processObject.getMessageInfo().getMessageId(), sender,
					CodeUtil.generateSimilarInfoHashString(id, config.getMain().getSimilarNodeIdNum()),
					config.getMain().getToken(), nodes, index);
			//加入路由表
			routingTables.get(index).put(new Node(id, sender, NodeRankEnum.GET_PEERS.getCode()));


			//开始查找任务
			//fetchMetadataByOtherWebTask.put(CodeUtil.bytes2HexStr(infoHash));
			return true;

	}

	@Override
	boolean isProcess(ProcessObject processObject) {
		return MethodEnum.GET_PEERS.equals(processObject.getMessageInfo().getMethod()) && YEnum.QUERY.equals(processObject.getMessageInfo().getStatus());
	}
}
