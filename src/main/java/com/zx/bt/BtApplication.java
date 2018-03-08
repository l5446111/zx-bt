package com.zx.bt;

import com.zx.bt.config.Config;
import com.zx.bt.socket.UDPServer;
import com.zx.bt.task.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement
public class BtApplication implements CommandLineRunner{

	private final UDPServer udpServer;
	private final InitTask initTask;
	private final FindNodeTask findNodeTask;
	private final GetPeersTask getPeersTask;
	private final FetchMetadataByOtherWebTask fetchMetadataByOtherWebTask;
	private final FetchMetadataByPeerTask fetchMetadataByPeerTask;
	private final Config config;

	public BtApplication(UDPServer udpServer, InitTask initTask, FindNodeTask findNodeTask, GetPeersTask getPeersTask, FetchMetadataByOtherWebTask fetchMetadataByOtherWebTask, FetchMetadataByPeerTask fetchMetadataByPeerTask, Config config) {
		this.udpServer = udpServer;
		this.initTask = initTask;
		this.findNodeTask = findNodeTask;
		this.getPeersTask = getPeersTask;
		this.fetchMetadataByOtherWebTask = fetchMetadataByOtherWebTask;
		this.fetchMetadataByPeerTask = fetchMetadataByPeerTask;
		this.config = config;
	}

	public static void main(String[] args) {
		SpringApplication.run(BtApplication.class, args);
	}




	/**
	 * 启动任务
	 * 最高优先级
	 */
	@Order(Integer.MIN_VALUE)
	@Override
	public void run(String... strings) throws Exception {
		if(!config.getMain().getStart()) return;
		//异步启动udp服务端
		udpServer.start();
		//同步执行初始化任务
		initTask.run();
		//异步启动find_node任务
		findNodeTask.start();
		//异步启动get_peers任务
		getPeersTask.start();
		//异步启动fetchMetadataByOtherWeb任务
		fetchMetadataByOtherWebTask.start();
		//异步启动fetchMetadataByPeerTask任务
		fetchMetadataByPeerTask.start();

	}
}
