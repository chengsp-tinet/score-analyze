package com.csp.app.common;

import com.alibaba.druid.pool.DruidDataSource;
import com.csp.app.service.CacheService;
import com.csp.app.service.RedisService;
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
    private List<CacheService> cacheServices;
    @Autowired
    private SqlFilter sqlFilter;
    @Autowired
    private RedisService redisService;
    @PreDestroy
    private void doBeforeDestroy(){
        logger.info("项目关闭时执行的操作");
    }

    @PostConstruct
    private void doAfterStart(){
        logger.info("项目启动时执行的操作");
        logger.info("CacheService个数:{}",cacheServices.size());
        druidDataSource.setProxyFilters(Lists.newArrayList(sqlFilter));
        for (CacheService cacheService : cacheServices){
            cacheService.loadCache();
        }
        logger.info("缓存加载完毕,启动成功");
    }
}
