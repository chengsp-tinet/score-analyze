package com.csp.app.common;

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
                logger.info("刷新本地缓存...");
                ((CacheService) ContextUtil.getBean(message)).flushLocalCache();
            }
        } catch (Exception e) {
            logger.error("接受消息异常:{}", e);
        }
    }
}
