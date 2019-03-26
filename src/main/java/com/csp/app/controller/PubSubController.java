package com.csp.app.controller;

import com.csp.app.common.Const;
import com.csp.app.util.RedisUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * redis发布订阅者测试
 *
 * @author chengsp on 2019年3月25日16:38:33
 */
@RestController
@RequestMapping("pubsub")
public class PubSubController {
    @Autowired
    private RedisUtil redisUtil;

    @RequestMapping("test")
    public void testSubAndPub(String str) {
        redisUtil.sendMessage(Const.DEFAULT_CHANNEL, RandomStringUtils.randomAlphabetic(10));

    }
}
