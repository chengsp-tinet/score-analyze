package com.csp.app.service;

import com.baomidou.mybatisplus.service.IService;
import com.csp.app.common.CacheService;
import com.csp.app.entity.Clasz;

import java.util.List;

public interface ClassService extends IService<Clasz> , CacheService<Clasz> {
    List<Clasz>  searchAll();
}
