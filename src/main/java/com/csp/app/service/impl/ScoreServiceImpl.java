package com.csp.app.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.csp.app.entity.Score;
import com.csp.app.mapper.ScoreMapper;
import com.csp.app.mapper.SystemSettingMapper;
import com.csp.app.service.ScoreService;
import com.csp.app.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author chengsp
 */
@Service
public class ScoreServiceImpl extends ServiceImpl<ScoreMapper, Score> implements ScoreService {
    @Autowired
    private SystemSettingMapper systemSettingMapper;
    @Autowired
    private RedisUtil redisUtil;

}
