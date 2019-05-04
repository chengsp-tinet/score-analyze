package com.csp.app.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.csp.app.common.CacheKey;
import com.csp.app.common.Const;
import com.csp.app.entity.Clasz;
import com.csp.app.entity.Student;
import com.csp.app.mapper.StudentMapper;
import com.csp.app.service.ClassService;
import com.csp.app.service.RedisService;
import com.csp.app.service.StudentService;
import com.google.common.collect.ArrayListMultimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.util.StringUtil;

import java.util.Collection;
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
    private StudentMapper studentMapper;
    @Autowired
    private RedisService redisService;
    @Autowired
    private ClassService classService;

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
        List<Student> students = studentMapper.selectList(null);
        for (Student student : students) {
            redisService.setObject(String.format(CacheKey.STUDENT_ID_STUDENT, student.getStudentId())
                    , student, Const.DEFAULT_INDEX);
        }
        logger.info("缓存Student{}条", students.size());
    }

    @Override
    public void flushLocalCache() {
        localCache.clear();
        logger.info("清空本地缓存{}条", localCache.size());
    }

    @Override
    public boolean add(Student student) {
        completeEntity(student);
        return insert(student);
    }

    private void completeEntity(Student student) {
        Integer classId = student.getClassId();
        Clasz clasz = classService.getEntityFromLocalCacheByKey(String.format(CacheKey.CLASS_ID_CLASS
                , classId));
        if (clasz == null) {
            throw new RuntimeException("不存在这样的班级,班级id" + classId);
        } else {
            Long maxStudentId = studentMapper.selectMaxStudentIdByClassId(classId);
            Long insertStudentId = maxStudentId;
            if (maxStudentId == null) {
                insertStudentId = classId * 1000L;
            } else {
                insertStudentId = maxStudentId + 1;
            }
            student.setStudentId(insertStudentId);
            student.setToSchoolYear(clasz.getToSchoolYear());
            student.setType(clasz.getType());
        }
    }

    @Override
    public boolean batchAdd(List<Student> students) {
        ArrayListMultimap<Integer, Student> multiMap = ArrayListMultimap.create();
        for (Student student : students) {
            multiMap.put(student.getClassId(), student);
        }
        Map<Integer, Collection<Student>> classfyMap = multiMap.asMap();
        classfyMap.forEach((classId, studentList) -> {
            Clasz clasz = classService.getEntityFromLocalCacheByKey(String.format(CacheKey.CLASS_ID_CLASS
                , classId));
            Long maxStudentId = studentMapper.selectMaxStudentIdByClassId(classId);
            Long insertStudentId;
            if (maxStudentId == null) {
                insertStudentId = classId * 1000L;
            } else {
                insertStudentId = maxStudentId + 1;
            }
            int i = 1;
            for (Student student : studentList) {
                student.setStudentId(insertStudentId + i);
                student.setToSchoolYear(clasz.getToSchoolYear());
                student.setType(clasz.getType());
                i++;
            }
        });
        return insertBatch(students);
    }

    @Override
    public List<Object> selectStudentsByExamGroupId(Integer examGroupId){
        EntityWrapper<Student> wrapper = new EntityWrapper<>();
        wrapper.setSqlSelect("student_id");
        return selectObjs(wrapper);
    }

    @Override
    public Page<Student> searchSelectivePage(Student student, Integer page, Integer limit, String studentName
            , String orderFiled, String orderType){
        if (page == null) {
            page = 0;
        }
        if (limit == null) {
            limit = 10;
        }
        student.setStudentName(null);
        EntityWrapper<Student> wrapper = new EntityWrapper<>(student);
        if (StringUtil.isNotEmpty(studentName)) {
            wrapper.like("student_name", "%" + studentName + "%");
        }
        boolean orderTypeBoo = false;
        if (StringUtil.isNotEmpty(orderType) && orderType.equals("asc")) {
            orderTypeBoo = true;
        }
        if (StringUtil.isEmpty(orderFiled)) {
            orderFiled = "id";
        }
        int count = selectCount(wrapper);
        wrapper.orderBy(orderFiled,orderTypeBoo);
        Page<Student> studentPage = new Page<>(page,limit);
        studentPage.setTotal(count);
        selectPage(studentPage, wrapper);
        return studentPage;
    }
}
