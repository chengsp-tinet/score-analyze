package com.csp.app.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.csp.app.common.CacheKey;
import com.csp.app.common.Const;
import com.csp.app.entity.Course;
import com.csp.app.entity.ExamGroup;
import com.csp.app.mapper.ExamGroupMapper;
import com.csp.app.service.ExamGroupService;
import com.csp.app.service.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.util.StringUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author chengsp
 */
@Service
public class ExamGroupServiceImpl extends ServiceImpl<ExamGroupMapper, ExamGroup> implements ExamGroupService {
    private final static Logger logger = LoggerFactory.getLogger(ExamGroupServiceImpl.class);
    private static final ExamGroup NULL_ENTITY = new ExamGroup();
    private static Map<String, ExamGroup> localCache = new ConcurrentHashMap<>(32);
    @Autowired
    private ExamGroupMapper examGroupMapper;
    @Autowired
    private RedisService redisService;

    @Override
    public ExamGroup getEntityFromLocalCacheByKey(String key) {
        ExamGroup localEntity = localCache.get(key);
        if (localEntity == null) {
            ExamGroup redisEntity = redisService.getObject(key, Const.DEFAULT_INDEX, ExamGroup.class);
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
        List<ExamGroup> examGroups = examGroupMapper.selectList(null);
        for (ExamGroup examGroup : examGroups) {
            redisService.setObject(String.format(CacheKey.EXAM_GROUP_ID_EXAM_GROUP, examGroup.getExamGroupId())
                    , examGroup, Const.DEFAULT_INDEX);
            redisService.setObject(String.format(CacheKey.EXAM_GROUP_NAME_EXAM_GROUP, examGroup.getExamGroupName())
                    , examGroup, Const.DEFAULT_INDEX);
        }
        redisService.setObject(CacheKey.COURSE_ALL, examGroups, Const.DEFAULT_INDEX);
        logger.info("examGroup {}条", examGroups.size());
    }

    @Override
    public void flushLocalCache() {
        localCache.clear();
        logger.info("清空本地缓存 {}条", localCache.size());
    }

    @Override
    public List<ExamGroup> searchAll() {
        String coursesStr = redisService.getString(CacheKey.EXAM_GROUP_ALL, Const.DEFAULT_INDEX);
        List<ExamGroup> courses;
        if (StringUtil.isNotEmpty(coursesStr)) {
            courses = JSON.parseArray(coursesStr, ExamGroup.class);
        } else {
            courses = examGroupMapper.selectList(null);
            redisService.setObject(CacheKey.EXAM_GROUP_ALL, courses, Const.DEFAULT_INDEX);
        }
        return courses;
    }

    @Override
    public boolean add(ExamGroup examGroup) {
        if (examGroup.getExamGroupId() == null) {
            Integer maxExamGroupId = examGroupMapper.selectMaxExamGroupId();
            Integer examGroupId;
            if (maxExamGroupId == null) {
                examGroupId = Const.INIT_EXAM_GROUP_ID;
            } else {
                examGroupId = maxExamGroupId + 1;
            }
            examGroup.setExamGroupId(examGroupId);
        }
        return insert(examGroup);
    }

    @Override
    public boolean batchAdd(List<ExamGroup> examGroups) {
        if (CollectionUtils.isNotEmpty(examGroups)) {
            Integer maxCourseId = examGroupMapper.selectMaxExamGroupId();
            Integer examGroupId;
            if (maxCourseId == null) {
                examGroupId = Const.INIT_EXAM_GROUP_ID;
            } else {
                examGroupId = maxCourseId + 1;
            }
            int i = 0;
            for (ExamGroup examGroup : examGroups) {
                if (examGroup.getExamGroupId() != null) {
                    continue;
                }
                examGroup.setExamGroupId(examGroupId + i);
                i++;
            }
            return insertBatch(examGroups);
        }
        return true;
    }

}
