package com.csp.app.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.csp.app.common.CacheKey;
import com.csp.app.common.Const;
import com.csp.app.entity.Course;
import com.csp.app.mapper.CourseMapper;
import com.csp.app.service.CourseService;
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
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {
    private final static Logger logger = LoggerFactory.getLogger(CourseServiceImpl.class);
    private static final Course NULL_ENTITY = new Course();
    private static Map<String, Course> localCache = new ConcurrentHashMap<>(32);
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private RedisService redisService;

    @Override
    public Course getEntityFromLocalCacheByKey(String key) {
        Course localEntity = localCache.get(key);
        if (localEntity == null) {
            Course redisEntity = redisService.getObject(key, Const.DEFAULT_INDEX, Course.class);
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
        List<Course> courses = courseMapper.selectList(null);
        for (Course course : courses) {
            redisService.setObject(String.format(CacheKey.COURSE_ID_COURSE, course.getCourseId())
                    , course, Const.DEFAULT_INDEX);
            redisService.setObject(String.format(CacheKey.COURSE_NAME_COURSE, course.getCourseName())
                    , course, Const.DEFAULT_INDEX);
        }
        redisService.setObject(CacheKey.COURSE_ALL, courses, Const.DEFAULT_INDEX);
        logger.info("缓存Course{}条", courses.size());
    }

    @Override
    public void flushLocalCache() {
        localCache.clear();
        logger.info("清空本地缓存{}条", localCache.size());
    }

    @Override
    public List<Course> searchAll() {
        String coursesStr = redisService.getString(CacheKey.COURSE_ALL, Const.DEFAULT_INDEX);
        List<Course> courses;
        if (StringUtil.isNotEmpty(coursesStr)) {
            courses = JSON.parseArray(coursesStr, Course.class);
        } else {
            courses = courseMapper.selectList(null);
            redisService.setObject(CacheKey.COURSE_ALL, courses, Const.DEFAULT_INDEX);
        }
        return courses;
    }

    @Override
    public boolean batchAdd(List<Course> courses) {
        if (CollectionUtils.isNotEmpty(courses)) {
            Integer maxCourseId = courseMapper.selectMaxCourseId();
            Integer courseId;
            if (maxCourseId == null) {
                courseId = Const.INIT_COURSE_ID;
            } else {
                courseId = maxCourseId + 1;
            }
            int i = 0;
            for (Course course : courses) {
                course.setCourseId(courseId + i);
                i++;
            }
            return insertBatch(courses);
        }
        return true;
    }

    @Override
    public boolean insert(Course entity) {
        Integer maxCourseId = courseMapper.selectMaxCourseId();
        if (maxCourseId == null) {
            maxCourseId = Const.INIT_COURSE_ID;
        }
        entity.setCourseId(maxCourseId + 1);
        return super.insert(entity);
    }
}
