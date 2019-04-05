package com.csp.app.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 项目启动后会立即执行
 *
 * @author chengsp on 2019年3月25日16:28:12
 */
@Component
public class InitComponent implements CommandLineRunner {

    private static Logger logger = LoggerFactory.getLogger(InitComponent.class);

    @Override
    public void run(String... arg0) {
        new SubThread("订阅线程").start();
        logger.info("启动成功......");
    }

}