package com.csp.app.common;

import com.csp.app.service.SystemSettingService;
import com.csp.app.util.ContextUtil;
import com.csp.app.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 项目启动后会立即执行
 */
@Component
public class RedisDataLoad implements CommandLineRunner {

    private static Logger logger = LoggerFactory.getLogger(RedisDataLoad.class);

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public void run(String... arg0) {
        logger.info("加载数据库数据到缓存...");
        /*SystemSettingService systemSettingService = ContextUtil.getBean(SystemSettingService.class);
        systemSettingService.redisLoad(null);*/

    }

}