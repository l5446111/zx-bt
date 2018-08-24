package com.zx.bt.hashspider.service;

import com.zx.bt.common.entity.InfoHash;
import com.zx.bt.common.util.SleepUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class QueueServiceTest {
    @Autowired
    private QueueService queueService;

    @Test
    public void init() throws Exception {
        for (int i = 0; i < 100; i++) {
            queueService.pushInfoHash(new InfoHash("" + i, i * i + ""));
        }
        while(!queueService.isClean()){
            SleepUtil.sleep(1000);
        }
    }

}