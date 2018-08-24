package com.zx.bt.hashspider.service;

import com.zx.bt.common.entity.InfoHash;
import com.zx.bt.common.util.SleepUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
@Slf4j
public class QueueService {
    private BlockingQueue<Object> infoHashQueue;
    @Autowired
    private RedisTemplate redisTemplate;

    public boolean isClean() {
        return infoHashQueue == null && infoHashQueue.size() == 0;
    }

    public synchronized void pushInfoHash(Serializable obj) {
        try {
            infoHashQueue.put(obj);
        } catch (InterruptedException e) {
            log.error("入java队列异常,{}", obj, e);
        }
    }

    @PostConstruct
    public void init() {
        infoHashQueue = new LinkedBlockingQueue<>(5000);

        new Thread(() -> {
            startAsynCache();
        }).start();
    }

    public void startAsynCache() {
        long sleepTime = 5000L;
        ListOperations listOperations = redisTemplate.opsForList();
        //启动异步放入缓存线程
        Object before = null;
        Object obj = null;
        int contineExceptionCount = 0;
        while (true) {
            try {
                if (before != null) {
                    obj = before;
                    before = null;
                    log.info("入redis队列异常，重新入队,{}", obj);
                } else {
                    obj = infoHashQueue.take();
                }

                if (obj == null) {
                    log.info("java出队列为空");
                    SleepUtil.sleep(sleepTime);
                    continue;
                }
                listOperations.leftPush("SYSTEM_BT_DHT_QUEUE", obj);
                contineExceptionCount = 0;
            } catch (Exception e) {
                if (contineExceptionCount < 3600) {
                    ++contineExceptionCount;
                }
                log.error("入redis队列异常", e);
                SleepUtil.sleep(sleepTime + 500 * contineExceptionCount);
                before = obj;
            }
        }
    }
}
