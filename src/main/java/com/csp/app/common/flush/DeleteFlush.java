package com.csp.app.common.flush;

import com.csp.app.common.CacheFlush;
import com.csp.app.common.Const;
import com.csp.app.entity.SynMessage;
import com.csp.app.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeleteFlush implements CacheFlush {
    @Autowired
    private RedisService redisService;

    @Override
    public void doFlush(SynMessage synMessage) {
        redisService.delete(synMessage.getKey(), Const.DEFAULT_INDEX);
    }
}
