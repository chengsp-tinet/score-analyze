package com.csp.app.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.csp.app.common.CacheKey;
import com.csp.app.common.Const;
import com.csp.app.entity.SystemSetting;
import com.csp.app.mapper.SystemSettingMapper;
import com.csp.app.service.RedisService;
import com.csp.app.service.SystemSettingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author chengsp
 */
@Service
public class SystemSettingServiceImpl extends ServiceImpl<SystemSettingMapper, SystemSetting> implements SystemSettingService {
    private final static Logger logger = LoggerFactory.getLogger(SystemSettingServiceImpl.class);
    private static final SystemSetting NULL_ENTITY = new SystemSetting();
    private static Map<String, SystemSetting> localCache = new ConcurrentHashMap<>(32);
    @Autowired
    private SystemSettingMapper systemSettingMapper;
    @Autowired
    private RedisService redisService;

    @Override
    public List<SystemSetting> selectList(Wrapper<SystemSetting> wrapper) {
        return JSONArray.parseArray(redisService.getString("systemSetting.all", 1), SystemSetting.class);
    }

    @Override
    public SystemSetting getEntityFromLocalCacheByKey(String key) {
        SystemSetting localEntity = localCache.get(key);
        if (localEntity == null) {
            SystemSetting redisEntity = redisService.getObject(key, 1, SystemSetting.class);
            if (redisEntity == null) {
                localCache.put(key, NULL_ENTITY);
                return null;
            } else {
                localCache.put(key, redisEntity);
                return redisEntity;
            }
        } else {
            return localEntity == NULL_ENTITY ? null : localEntity;
        }
    }

    @Override
    public void loadCache() {
        List<SystemSetting> systemSettings = systemSettingMapper.selectList(null);
        for (SystemSetting systemSetting : systemSettings) {
            redisService.set(String.format(CacheKey.SYSTEM_SETTING_NAME_SYSTEM_SETTING, systemSetting.getName())
                    , systemSetting, Const.DEFAULT_INDEX);
        }
        logger.info("缓存SystemSetting{}条", systemSettings.size());
    }

    @Override
    public void flushLocalCache() {
        localCache.clear();
        logger.info("清空本地缓存{}条", localCache.size());

    }
}
