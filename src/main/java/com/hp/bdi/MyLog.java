package com.hp.bdi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;

/**
 * Created by golaniz on 10/07/2016.
 */
public class MyLog {
    public static void logme(String s) {
        Logger logger = LoggerFactory.getLogger(MyLog.class);
        logger.error("~~~MyLog~~~ ["+ ManagementFactory.getRuntimeMXBean().getName()+"] ["+Thread.currentThread().getId()+"] " + s);
    }
}
