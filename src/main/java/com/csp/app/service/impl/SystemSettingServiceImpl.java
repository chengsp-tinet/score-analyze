package com.csp.app.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.csp.app.entity.SystemSetting;
import com.csp.app.mapper.SystemSettingMapper;
import com.csp.app.service.SystemSettingService;
import com.csp.app.util.RedisUtil;
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
    private static final String KEY = "sa.SystemSetting.name.%s";
    private static final SystemSetting NULL_ENTITY = new SystemSetting();
    private static Map<String, SystemSetting> localCache = new ConcurrentHashMap<>(32);
    @Autowired
    private SystemSettingMapper systemSettingMapper;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public List<SystemSetting> selectList(Wrapper<SystemSetting> wrapper) {
        return JSONArray.parseArray(redisUtil.getString("systemSetting.all", 1), SystemSetting.class);
    }

    @Override
    public String getKey(Object... args) {
        return String.format(KEY, args[0]);
    }

    @Override
    public SystemSetting getEntityFromLocalCacheByKey(String key) {
        SystemSetting localEntity = localCache.get(key);
        if (localEntity == null) {
            SystemSetting redisEntity = redisUtil.getObject(key, 1, SystemSetting.class);
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
            redisUtil.set(getKey(systemSetting.getName()), systemSetting, 1);
        }
        logger.info("缓存SystemSetting{}条", systemSettings.size());
    }

    @Override
    public void flushLocalCache() {
        logger.info("清空本地缓存{}条",localCache.size());
        localCache.clear();

    }
}
