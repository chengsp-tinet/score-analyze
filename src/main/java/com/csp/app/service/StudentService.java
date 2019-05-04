package com.csp.app.service;

import com.baomidou.mybatisplus.plugins.Page;
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
    /**
     * 查询那些学生参加了对应的考试
     * @param examGroupId
     * @return
     */
    List<Object> selectStudentsByExamGroupId(Integer examGroupId);

    /**
     * 分页查询
     * @param student
     * @param page
     * @param limit
     * @param studentName
     * @param orderFiled
     * @param orderType
     * @return
     */
    Page<Student> searchSelectivePage(Student student, Integer page, Integer limit, String studentName
            , String orderFiled, String orderType);
}
