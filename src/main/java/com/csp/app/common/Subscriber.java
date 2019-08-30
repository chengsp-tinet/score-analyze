package com.csp.app.common;

import com.alibaba.fastjson.JSON;
import com.csp.app.entity.SynMessage;
import com.csp.app.service.CacheService;
import com.csp.app.util.ContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPubSub;

@Component
public class Subscriber extends JedisPubSub {
    private final static Logger logger = LoggerFactory.getLogger(Subscriber.class);

    @Override
    public void onMessage(String channel, String message) {
        try {
            if (Const.DEFAULT_CHANNEL.equals(channel)) {
                SynMessage synMessage = JSON.parseObject(message, SynMessage.class);
                synMessage.getFlushType().flush(synMessage);
                /*CacheService cacheService = (CacheService) ContextUtil.getBean(message);
                cacheService.flushLocalCache(null);*/
            }
        } catch (Exception e) {
            logger.error("刷新缓存异常:{}", e);
        }
    }
}
