package com.csp.app.common.flush;

import com.csp.app.common.CacheFlush;
import com.csp.app.common.Const;
import com.csp.app.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;

public class InsertFlush implements CacheFlush {
    @Autowired
    private RedisService redisService;
    @Override
    public void doFlush(String key, Object data) {
        redisService.setObject(key,data, Const.DEFAULT_INDEX);
    }
}
