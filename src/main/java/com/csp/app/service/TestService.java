package com.csp.app.service;

import com.baomidou.mybatisplus.service.IService;
import com.csp.app.entity.TestEntity;

import java.util.List;

public interface TestService extends IService<TestEntity>, CacheService<TestEntity> {
    public List<TestEntity> selectPart();
}
