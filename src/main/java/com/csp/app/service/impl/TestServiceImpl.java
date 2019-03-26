package com.csp.app.service.impl;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.csp.app.entity.TestEntity;
import com.csp.app.mapper.TestMapper;
import com.csp.app.mapper.SystemSettingMapper;
import com.csp.app.service.TestService;
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
public class TestServiceImpl extends ServiceImpl<TestMapper, TestEntity> implements TestService {
    private static Logger logger = LoggerFactory.getLogger(TestServiceImpl.class);
    @Autowired
    private SystemSettingMapper systemSettingMapper;
    @Autowired
    private TestMapper testMapper;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public List<TestEntity> selectList(Wrapper<TestEntity> wrapper) {
        return super.selectList(wrapper);
    }

    @Override
    public List<TestEntity> selectPart() {
        return testMapper.selectPart();
    }

    @Override
    public void loadCache(TestEntity testEntity) {

    }
}
