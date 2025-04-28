package com.springstudy.backend.Common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

public class LogUtil {

    public static final Logger logger = LoggerFactory.getLogger(LogUtil.class);

    public static void error(Class<?> className, String msg, Throwable e){
        logger.error("{}의 {}: {}", className, msg, e.getMessage());
    }
    public static void error(Class<?> className, String msg){
        logger.error("{}의 {}", className, msg);
    }
//    private static String timestamp() {
//        return DateTimeFormatter.ISO_INSTANT.format(Instant.now());
//    }
}
