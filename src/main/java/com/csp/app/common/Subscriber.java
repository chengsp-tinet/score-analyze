package com.csp.app.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPubSub;

public class Subscriber extends JedisPubSub {
    private final static Logger logger = LoggerFactory.getLogger(Subscriber.class);

    @Override
    public void onMessage(String channel, String message) {
        logger.info("接收到:{}发布的消息:{}", channel, message);
    }
}
