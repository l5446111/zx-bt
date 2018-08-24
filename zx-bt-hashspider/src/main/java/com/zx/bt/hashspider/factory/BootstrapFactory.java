package com.zx.bt.hashspider.factory;

import com.zx.bt.hashspider.config.Config;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * author:ZhengXing
 * datetime:2018-01-23 20:10
 * {@link Bootstrap} 类 工厂
 * 构建TCP连接
 */
@Component
public class BootstrapFactory {
    private  final Bootstrap bootstrap;

    @Autowired
    public BootstrapFactory(Config config) {
        this.bootstrap = new Bootstrap()
                .group(new NioEventLoopGroup(config.getPerformance().getTcpClientThreadNum()))
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, config.getPerformance().getTcpConnectTimeoutMs())
                .option(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(1, 102400, Integer.MAX_VALUE));
    }

    public Bootstrap build() {
        return bootstrap.clone();
    }





}
