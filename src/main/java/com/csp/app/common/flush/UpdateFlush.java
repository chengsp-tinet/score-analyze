package com.csp.app.common.flush;

import com.alibaba.fastjson.JSON;
import com.csp.app.common.CacheFlush;
import com.csp.app.common.Const;
import com.csp.app.entity.SynMessage;
import com.csp.app.service.CacheService;
import com.csp.app.service.RedisService;
import com.csp.app.util.ContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UpdateFlush implements CacheFlush {
    @Autowired
    private RedisService redisService;

    @Override
    public void doFlush(SynMessage synMessage) {
        if (synMessage.getKey() != null) {
            redisService.publish(Const.DEFAULT_CHANNEL, JSON.toJSONString(synMessage.getData()));
        } else {
            CacheService cacheService = (CacheService) ContextUtil.getBean(synMessage.getBeanName());
            cacheService.loadCache();
        }
    }
}
