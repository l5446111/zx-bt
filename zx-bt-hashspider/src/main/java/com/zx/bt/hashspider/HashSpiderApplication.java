package com.zx.bt.hashspider;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zx.bt.hashspider.config.Config;
import com.zx.bt.hashspider.task.FindNodeTask;
import com.zx.bt.hashspider.task.InitTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.annotation.Order;

import java.util.Collection;

@SpringBootApplication
@Slf4j
public class HashSpiderApplication implements CommandLineRunner {

    private final InitTask initTask;
    private final FindNodeTask findNodeTask;
    private Config config;

    public HashSpiderApplication(InitTask initTask, FindNodeTask findNodeTask, Config config,
                                 ObjectMapper objectMapper) {
        this.initTask = initTask;
        this.findNodeTask = findNodeTask;
        this.config = config;
        this.objectMapper = objectMapper;
    }

    public static void main(String[] args) {
        SpringApplication.run(HashSpiderApplication.class, args);
    }

    /**
     * 结束程序
     */
    public static void exit() {
        System.exit(0);
    }


    private final ObjectMapper objectMapper;

    /**
     * 获取泛型的Collection Type
     */
    public <C extends Collection, E> JavaType getCollectionType(Class<C> collectionClass, Class<E> elementClass) {
        return objectMapper.getTypeFactory().constructCollectionType(collectionClass, elementClass);
    }


    /**
     * 启动任务
     * 最高优先级
     */
    @Order(Integer.MIN_VALUE)
    @Override
    public void run(String... strings) throws Exception {
        //同步执行初始化任务
        initTask.run();
        //异步启动find_node任务
        findNodeTask.start();


    }
}
