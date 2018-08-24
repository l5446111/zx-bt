/*
package com.zx.bt.hashspider.task;

import com.zx.bt.common.store.CommonCache;
import com.zx.bt.common.util.CodeUtil;
import com.zx.bt.hashspider.config.Config;
import com.zx.bt.hashspider.dto.GetPeersSendInfo;
import com.zx.bt.hashspider.function.Pauseable;
import com.zx.bt.hashspider.socekt.Sender;
import com.zx.bt.hashspider.store.RoutingTable;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

*/
/**
 * author:ZhengXing
 * datetime:2018/2/27 0027 16:06
 * 发送get_peers请求,以获取目标主机
 *//*

@Component
@Slf4j
public class GetPeersTask implements Pauseable {
	private static final String LOG = "[GetPeersTask]";


	//(消息id,info_hash)缓存
	private final CommonCache<GetPeersSendInfo> getPeersCache;
	private final List<RoutingTable> routingTables;
	private final Config config;
	private final ReentrantLock lock;
	private final Condition condition;
	private final List<String> nodeIds;
	private final Sender sender;
	private final FetchMetadataByPeerTask fetchMetadataByPeerTask;
	private final int getPeersTaskExpireSecond;

	public GetPeersTask(CommonCache<GetPeersSendInfo> getPeersCache, List<RoutingTable> routingTables,
                        Config config, Sender sender, FetchMetadataByPeerTask fetchMetadataByPeerTask) {
		this.getPeersCache = getPeersCache;
		this.routingTables = routingTables;
		this.config = config;
		this.queue = new LinkedBlockingQueue<>(config.getPerformance().getGetPeersTaskInfoHashQueueLen());
		this.sender = sender;
		this.fetchMetadataByPeerTask = fetchMetadataByPeerTask;
		this.lock = new ReentrantLock();
		this.condition = this.lock.newCondition();
		this.nodeIds = config.getMain().getNodeIds();
		this.getPeersTaskExpireSecond = config.getPerformance().getGetPeersTaskExpireSecond();

	}

	//info_hash等待队列
	private final BlockingQueue<String> queue;

	*/
/**
	 * 入队
	 * 此处的入口仅为otherWeb任务, 已经去重过. 此处不做去重处理
	 *//*

	public void put(String infoHashHexStr) {
		queue.offer(infoHashHexStr);
	}


	*/
/**
	 * 删除某个任务
	 *//*

	public void remove(String infoHashHexStr) {
		queue.remove(infoHashHexStr);
	}

	*/
/**
	 * 长度
	 *//*

	public int size() {
		return queue.size();
	}

	*/
/**
	 * 任务线程
	 *//*

	public void start() {
		final int getPeersTaskCreateIntervalMillisecond = config.getPerformance().getGetPeersTaskCreateIntervalMs();
		final int getPeersTaskPauseSecond = config.getPerformance().getGetPeersTaskPauseSecond();
		new Thread(() -> {
			while (true) {
				try {
					//如果当前查找任务过多. 暂停30s再继续
					if (getPeersCache.size() > config.getPerformance().getGetPeersTaskConcurrentNum()) {
						log.info("{}当前任务数过多,暂停获取新任务线程{}s.", LOG, getPeersTaskPauseSecond);
						pause(lock, condition, getPeersTaskPauseSecond, TimeUnit.SECONDS);
						continue;
					}
					//开启新任务
					run(queue.take());
					//开始一个任务后,暂停
					pause(lock, condition, getPeersTaskCreateIntervalMillisecond, TimeUnit.MILLISECONDS);
				} catch (Exception e) {
					log.error("{}异常.e:{}", LOG, e.getMessage(), e);
				}
			}
		}).start();
	}


	*/
/**
	 * 开始任务
	 *//*

	private void run(String infoHashHexStr) {
		//消息id
		String messageId = BTUtil.generateMessageIDOfGetPeers();
//		log.info("{}开始新任务.消息Id:{},infoHash:{}", LOG, messageId, infoHashHexStr);

		//当前已发送节点id
		List<byte[]> nodeIdList = new ArrayList<>();
		for (int i = 0; i < routingTables.size(); i++) {
			//获取最近的8个地址
			List<Node> nodeList = routingTables.get(i).getForTop8(CodeUtil.hexStr2Bytes(infoHashHexStr));
			//目标nodeId
			nodeIdList.addAll(nodeList.stream().map(Node::getNodeIdBytes).collect(Collectors.toList()));
			//目标地址
			List<InetSocketAddress> addresses = nodeList.stream().map(Node::toAddress).collect(Collectors.toList());
			//批量发送
			this.sender.getPeersBatch(addresses, nodeIds.get(i), new String(CodeUtil.hexStr2Bytes(infoHashHexStr), CharsetUtil.ISO_8859_1), messageId, i);
		}
		//存入缓存
		getPeersCache.put(messageId, new GetPeersSendInfo(infoHashHexStr).put(nodeIdList));
		//存入任务队列
		fetchMetadataByPeerTask.put(infoHashHexStr,getStartTime());
	}


	*/
/**
	 * 根据缓存过期时间,计算fetchMetadataByPeerTask任务开始时间
	 *//*

	public long getStartTime() {
		return System.currentTimeMillis() + getPeersTaskExpireSecond * 1000;
	}



}
*/
