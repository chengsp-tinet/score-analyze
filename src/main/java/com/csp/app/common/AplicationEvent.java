package com.csp.app.common;

import com.alibaba.druid.pool.DruidDataSource;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
@Component
public class AplicationEvent {
    private final static Logger logger = LoggerFactory.getLogger(AplicationEvent.class);
    @Autowired
    private DruidDataSource druidDataSource;
    @Autowired
    List<CacheService> cacheServices;
    @Autowired
    private SqlFilter sqlFilter;

    @PreDestroy
    private void doBeforeDestroy(){
        logger.info("项目关闭时执行的操作");
    }

    @PostConstruct
    private void doAfterStart(){
        logger.info("项目启动时执行的操作");
        druidDataSource.setProxyFilters(Lists.newArrayList(sqlFilter));
        for (CacheService cacheService : cacheServices){
            cacheService.redisLoad(null);
        }
    }
}