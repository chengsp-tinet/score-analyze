package com.csp.app.service;

import com.baomidou.mybatisplus.service.IService;
import com.csp.app.common.CacheService;
import com.csp.app.entity.Student;

import java.util.List;

public interface StudentService extends IService<Student>, CacheService<Student> {
    /**
     * 添加学生
     * @param student
     * @return
     */
    boolean add(Student student);

    /**
     * 批量添加学生
     * @param students
     * @return
     */
    boolean batchAdd(List<Student> students);
}
