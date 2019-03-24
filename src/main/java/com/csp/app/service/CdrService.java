package com.csp.app.service;

import com.baomidou.mybatisplus.service.IService;
import com.csp.app.common.CacheService;
import com.csp.app.entity.Cdr;

import java.util.List;

public interface CdrService extends IService<Cdr>, CacheService<Cdr> {
    public List<Cdr> selectPart();
}
