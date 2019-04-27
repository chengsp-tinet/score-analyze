package com.csp.app.service;

import com.baomidou.mybatisplus.service.IService;
import com.csp.app.common.CacheService;
import com.csp.app.entity.Score;

import java.util.List;

public interface ScoreService extends IService<Score>{
    /**
     * 添加成绩记录
     * @param score
     * @return
     */
    boolean add(Score score);

    /**
     * 批量录入成绩
     * @param scores
     * @return
     */
    boolean batchAdd(List<Score> scores);
}
