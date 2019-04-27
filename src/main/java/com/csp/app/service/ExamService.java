package com.csp.app.service;

import com.baomidou.mybatisplus.service.IService;
import com.csp.app.common.CacheService;
import com.csp.app.entity.Exam;

import java.util.List;

public interface ExamService extends IService<Exam>, CacheService<Exam> {
    List<Exam> searchAll();

    /**
     * 添加考试
     * @param exam
     * @return
     */
    boolean add(Exam exam);

    /**
     * 批量添加考试
     * @param exams
     * @return
     */
    boolean batchAdd(List<Exam> exams);
}
