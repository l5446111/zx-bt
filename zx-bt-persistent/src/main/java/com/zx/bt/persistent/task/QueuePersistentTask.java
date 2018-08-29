package com.zx.bt.persistent.task;

import com.zx.bt.hashspider.function.Pauseable;
import com.zx.bt.persistent.service.PersistentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class QueuePersistentTask implements Pauseable{
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private PersistentService persistentService;
    @PostConstruct
    public void persitentStart() {
        ListOperations listOperations = redisTemplate.opsForList();
        log.info("SYSTEM_BT_DHT_QUEUE size:{}",listOperations.size("SYSTEM_BT_DHT_QUEUE"));
        int i=0;
        while(true){
            ++i;
            Object obj = listOperations.rightPop("SYSTEM_BT_DHT_QUEUE");
            if(obj==null){
                try {
                    Thread.sleep(10000L);
                    log.info("sleep");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }
            log.info("处理第{}个",(i+1));
            persistentService.persistent(obj);
            if(i%1000==0){
                log.info("SYSTEM_BT_DHT_QUEUE size:{}",listOperations.size("SYSTEM_BT_DHT_QUEUE"));
            }
        }

    }
}
