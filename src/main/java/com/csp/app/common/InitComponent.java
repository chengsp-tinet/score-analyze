package com.csp.app.common;

import com.csp.app.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * 项目启动后会立即执行
 *
 * @author chengsp on 2019年3月25日16:28:12
 */
@Component
public class InitComponent implements CommandLineRunner {

    private static Logger logger = LoggerFactory.getLogger(InitComponent.class);
    @Autowired
    private JedisPool jedisPool;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public void run(String... arg0) {
        /*Jedis jedis = jedisPool.getResource();
        jedis.subscribe(new Subscriber(), Const.DEFAULT_CHANNEL);
        jedis.close();*/
        new SubThread("订阅线程").start();
        logger.info("启动成功......");
    }

}