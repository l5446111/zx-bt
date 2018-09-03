package com.zx.bt.persistent.task;

import com.zx.bt.hashspider.function.Pauseable;
import com.zx.bt.persistent.service.PersistentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class QueuePersistentTask implements Pauseable {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private PersistentService persistentService;
    private int i = 0;
    private ListOperations listOperations;

    @PostConstruct
    public void persitentStart() {
        listOperations = redisTemplate.opsForList();
        new Thread(() -> {
            log.info("SYSTEM_BT_DHT_QUEUE size:{}", listOperations.size("SYSTEM_BT_DHT_QUEUE"));
            LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(10000000);
            ThreadPoolExecutor pool = new ThreadPoolExecutor(100, 500, 1, TimeUnit.DAYS, queue);
            while (true) {
                pool.execute(() -> {
                    Object obj = null;
                    if ((obj = listOperations.rightPop("SYSTEM_BT_DHT_QUEUE")) == null) {
                        log.info("没有,sleep");
                        try {
                            Thread.sleep(5000L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                    addI();
                    try {
                        persistentService.persistent(obj);
                    }catch (RuntimeException e){
                        listOperations.leftPush("SYSTEM_BT_DHT_QUEUE", obj);
                        log.info("DataIntegrityViolationException");
                    } catch (Exception e) {
                        listOperations.leftPush("SYSTEM_BT_DHT_QUEUE", obj);

                        log.error("出现异常，睡眠", e);
                        try {
                            Thread.sleep(5000L);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    }
                });

            }
        }).start();
    }

    private synchronized void addI() {
        ++i;
        if (i % 100 == 0) {
            log.info("SYSTEM_BT_DHT_QUEUE处理数量:{}", i);
        }
        if (i % 1000 == 0) {
            log.info("SYSTEM_BT_DHT_QUEUE size:{}", listOperations.size("SYSTEM_BT_DHT_QUEUE"));
        }
    }
}
