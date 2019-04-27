package com.csp.app.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.csp.app.common.CacheKey;
import com.csp.app.common.Const;
import com.csp.app.entity.Course;
import com.csp.app.entity.Exam;
import com.csp.app.mapper.ExamMapper;
import com.csp.app.service.CourseService;
import com.csp.app.service.ExamService;
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
public class ExamServiceImpl extends ServiceImpl<ExamMapper, Exam> implements ExamService {
    private final static Logger logger = LoggerFactory.getLogger(ExamServiceImpl.class);
    private static final Exam NULL_ENTITY = new Exam();
    private static Map<String, Exam> localCache = new ConcurrentHashMap<>(32);
    @Autowired
    private ExamMapper examMapper;
    @Autowired
    private RedisService redisService;
    @Autowired
    private CourseService courseService;

    @Override
    public Exam getEntityFromLocalCacheByKey(String key) {
        Exam localEntity = localCache.get(key);
        if (localEntity == null) {
            Exam redisEntity = redisService.getObject(key, Const.DEFAULT_INDEX, Exam.class);
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
        List<Exam> exams = examMapper.selectList(null);
        for (Exam exam : exams) {
            redisService.setObject(String.format(CacheKey.EXAM_ID_EXAM, exam.getExamId())
                    , exam, Const.DEFAULT_INDEX);
            redisService.setObject(String.format(CacheKey.EXAM_NAME_EXAM, exam.getExamName())
                    , exam, Const.DEFAULT_INDEX);
        }
        redisService.setObject(CacheKey.EXAM_ALL, exams, Const.DEFAULT_INDEX);
        logger.info("缓存Exam{}条", exams.size());
    }

    @Override
    public void flushLocalCache() {
        localCache.clear();
        logger.info("清空本地缓存{}条", localCache.size());
    }

    @Override
    public List<Exam> searchAll() {

        String coursesStr = redisService.getString(CacheKey.EXAM_ALL, Const.DEFAULT_INDEX);
        List<Exam> exams;
        if (StringUtil.isNotEmpty(coursesStr)) {
            exams = JSON.parseArray(coursesStr, Exam.class);
        } else {
            exams = examMapper.selectList(null);
            redisService.setObject(CacheKey.EXAM_ALL, exams, Const.DEFAULT_INDEX);
        }
        return exams;
    }

    @Override
    public boolean add(Exam entity) {
        Integer maxExamId = examMapper.selectMaxExamId();
        if (maxExamId == null) {
            entity.setExamId(Const.INIT_EXAM_ID);
        } else {
            entity.setExamId(maxExamId + 1);
        }
        Integer courseId = entity.getCourseId();
        String courseName = entity.getCourseName();
        if (courseId == null) {
            Course course = courseService.getEntityFromLocalCacheByKey(String.format(CacheKey.COURSE_NAME_COURSE, courseName));
            if (course != null) {
                courseId = course.getCourseId();

            }
        }
        if (StringUtil.isEmpty(courseName)) {
            Course course = courseService.getEntityFromLocalCacheByKey(String.format(CacheKey.COURSE_ID_COURSE, courseId));
            if (course != null) {
                courseName = course.getCourseName();
            }
        }
        if (courseId == null || StringUtil.isEmpty(courseName)) {
            throw new RuntimeException("无法找到考试对应的科目,课程id或课程名称为空");
        }
        entity.setCourseId(courseId);
        entity.setCourseName(courseName);
        return super.insert(entity);
    }

}
