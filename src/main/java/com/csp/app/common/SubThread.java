package com.csp.app.common;

import com.csp.app.util.ContextUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class SubThread extends Thread {
    private JedisPool jedisPool = ContextUtil.getBean(JedisPool.class);
    public SubThread(String name) {
        super(name);
    }

    @Override
    public void run() {
        Jedis jedis = jedisPool.getResource();
        Subscriber subscriber = new Subscriber();
        jedis.subscribe(subscriber, Const.DEFAULT_CHANNEL);
        jedis.close();
    }
}
