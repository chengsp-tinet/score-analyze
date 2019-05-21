package com.csp.app.controller;

import com.csp.app.common.CacheService;
import com.csp.app.common.Const;
import com.csp.app.common.ResponseBuilder;
import com.csp.app.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class ReloadController {
    @Autowired
    private RedisService redisService;
    @Autowired
    private List<CacheService> cacheServices;

    @RequestMapping("/rl")
    @ResponseBody
    public ResponseBuilder reLoadCache() {
        redisService.flushDB(Const.DEFAULT_INDEX);
        for (CacheService cacheService : cacheServices) {
            cacheService.loadCache();
            cacheService.flushLocalCache();
        }
        return ResponseBuilder.buildSuccess("刷新缓存成功",null);
    }
}
