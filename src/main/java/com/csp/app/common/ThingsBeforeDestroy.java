package com.csp.app.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
@Service
public class ThingsBeforeDestroy {
    private final static Logger logger = LoggerFactory.getLogger(ThingsBeforeDestroy.class);
    @PreDestroy
    private void doBeforeDestroy(){
        logger.info("项目关闭时执行的操作");
    }
    @PostConstruct
    private void doAfterStart(){
        logger.info("项目启动时执行的操作");
    }
}
