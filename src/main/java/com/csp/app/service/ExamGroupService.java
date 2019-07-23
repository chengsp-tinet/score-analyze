package com.csp.app.service;

import com.baomidou.mybatisplus.service.IService;
import com.csp.app.entity.ExamGroup;
import com.csp.app.entity.Student;

import java.util.List;

public interface ExamGroupService extends IService<ExamGroup>, CacheService<ExamGroup> {
    /**
     * 添加学生
     * @param examGroup
     * @return
     */
    boolean add(ExamGroup examGroup);

    /**
     * 批量添加学生
     * @param examGroups
     * @return
     */
    boolean batchAdd(List<ExamGroup> examGroups);

    /**
     * 查询所有考试组
     * @return
     */
    List<ExamGroup> searchAll();
}
