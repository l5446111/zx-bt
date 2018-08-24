package com.zx.bt.hashspider.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zx.bt.common.enums.CacheMethodEnum;
import com.zx.bt.common.service.MetadataService;
import com.zx.bt.common.store.CommonCache;
import com.zx.bt.hashspider.dto.GetPeersSendInfo;
import com.zx.bt.hashspider.socekt.Sender;
import com.zx.bt.hashspider.socekt.UDPServer;
import com.zx.bt.hashspider.socekt.processor.UDPProcessor;
import com.zx.bt.hashspider.socekt.processor.UDPProcessorManager;
import com.zx.bt.hashspider.store.RoutingTable;
import com.zx.bt.hashspider.util.Bencode;
import com.zx.bt.hashspider.util.HttpClientUtil;
import io.netty.util.CharsetUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * author:ZhengXing
 * datetime:2018-02-17 13:57
 * 注入bean
 */
@Configuration
public class BeanConfig {
	//避免netty jar冲突
	static {		System.setProperty("es.set.netty.runtime.available.processors", "false"); }

	/**
	 * 初始化config的nodeIds
	 */
	@Autowired
	public void initConfigNodeIds(Config config) {
		config.getMain().initNodeIds();
	}

	/**
	 * Bencode编解码工具类
	 */
	@Bean
	public Bencode bencode() {
		return new Bencode();
	}

	/**
	 * get_peers请求消息缓存
	 */
	@Bean
	public CommonCache<GetPeersSendInfo> getPeersCache(Config config) {


		return new CommonCache<>(
				CacheMethodEnum.AFTER_WRITE,
				config.getPerformance().getGetPeersTaskExpireSecond(),
				config.getPerformance().getDefaultCacheLen());
	}

	/**
	 * udp 处理器管理器
	 * 可通过See{@link org.springframework.core.annotation.Order}改变处理器顺序
	 */
	@Bean
	public UDPProcessorManager udpProcessorManager(List<UDPProcessor> udpProcessors) {
		UDPProcessorManager udpProcessorManager = new UDPProcessorManager();
		udpProcessors.forEach(udpProcessorManager::register);
		return udpProcessorManager;
	}

	/**
	 * 创建多个路由表
	 */
	@Bean
	public List<RoutingTable> routingTables(Config config) {
		List<Integer> ports = config.getMain().getPorts();
		List<RoutingTable> result = new ArrayList<>(ports.size());
		List<String> nodeIds = config.getMain().getNodeIds();
		for (int i = 0; i < ports.size(); i++) {
			result.add(new RoutingTable(config, nodeIds.get(i).getBytes(CharsetUtil.ISO_8859_1), ports.get(i)));
		}
		return result;
	}

	/**
	 * udp handler类
	 */
	@Bean
	public List<UDPServer.UDPServerHandler> udpServerHandlers(Bencode bencode, Config config,
															  UDPProcessorManager udpProcessorManager,
															  Sender sender) {
		int size = config.getMain().getNodeIds().size();
		List<UDPServer.UDPServerHandler> result = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			result.add(new UDPServer.UDPServerHandler(i, bencode, udpProcessorManager, sender));
		}
		return result;
	}

}
