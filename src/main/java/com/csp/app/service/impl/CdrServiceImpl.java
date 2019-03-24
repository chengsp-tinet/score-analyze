package com.csp.app.service.impl;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.csp.app.entity.Cdr;
import com.csp.app.mapper.CdrMapper;
import com.csp.app.mapper.SystemSettingMapper;
import com.csp.app.service.CdrService;
import com.csp.app.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author chengsp
 */
@Service
public class CdrServiceImpl extends ServiceImpl<CdrMapper, Cdr> implements CdrService {
    private static Logger logger = LoggerFactory.getLogger(CdrServiceImpl.class);
    @Autowired
    private SystemSettingMapper systemSettingMapper;
    @Autowired
    private CdrMapper cdrMapper;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public List<Cdr> selectList(Wrapper<Cdr> wrapper) {
        return super.selectList(wrapper);
    }

    @Override
    public List<Cdr> selectPart() {
        return cdrMapper.selectPart();
    }

    @Override
    public void redisLoad(Cdr cdr) {

    }
}
