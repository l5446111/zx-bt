package com.zx.bt.common.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SleepUtil {
    public static void sleep(long time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            log.error("sleep异常",e);
        }
    }
}
