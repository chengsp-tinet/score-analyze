package com.csp.app.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.csp.app.common.CacheKey;
import com.csp.app.common.Const;
import com.csp.app.entity.Student;
import com.csp.app.mapper.StudentMapper;
import com.csp.app.service.RedisService;
import com.csp.app.service.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author chengsp
 */
@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {
    private final static Logger logger = LoggerFactory.getLogger(StudentServiceImpl.class);
    private static final Student NULL_ENTITY = new Student();
    private static Map<String, Student> localCache = new ConcurrentHashMap<>(32);
    @Autowired
    private StudentMapper classMapper;
    @Autowired
    private RedisService redisService;

    @Override
    public Student getEntityFromLocalCacheByKey(String key) {
        Student localEntity = localCache.get(key);
        if (localEntity == null) {
            Student redisEntity = redisService.getObject(key, Const.DEFAULT_INDEX, Student.class);
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
        List<Student> students = classMapper.selectList(null);
        for (Student student : students) {
            redisService.set(String.format(CacheKey.STUDENT_ID_STUDENT, student.getStudentId())
                    , student, Const.DEFAULT_INDEX);
        }
        logger.info("缓存Student{}条", students.size());
    }

    @Override
    public void flushLocalCache() {
        localCache.clear();
        logger.info("清空本地缓存{}条", localCache.size());
    }
}
