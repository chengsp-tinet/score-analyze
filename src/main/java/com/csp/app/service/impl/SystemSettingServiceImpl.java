package com.csp.app.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
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

/**
 * @author chengsp
 */
@Service
public class SystemSettingServiceImpl extends ServiceImpl<SystemSettingMapper, SystemSetting> implements SystemSettingService {
    private final static Logger logger = LoggerFactory.getLogger(SystemSettingServiceImpl.class);
    @Autowired
    private SystemSettingMapper systemSettingMapper;
    @Autowired
    private RedisUtil redisUtil;

    public void redisLoadAll() {
        redisUtil.set("systemSetting.all", JSON.toJSONString(systemSettingMapper.selectList(new EntityWrapper<>(new SystemSetting()))), 1);
    }

    @Override
    public boolean insert(SystemSetting entity) {
        boolean b = super.insert(entity);
        if (b) {
            redisLoadAll();
        }
        return b;
    }

    @Override
    public List<SystemSetting> selectList(Wrapper<SystemSetting> wrapper) {
        return JSONArray.parseArray(redisUtil.getString("systemSetting.all", 1), SystemSetting.class);
    }

    @Override
    public void loadCache(SystemSetting systemSetting) {
        logger.info(getClass()+"  加载缓存...");

    }
}
