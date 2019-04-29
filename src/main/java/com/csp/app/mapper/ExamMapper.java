package com.csp.app.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.csp.app.entity.Exam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface ExamMapper extends BaseMapper<Exam> {
    /**
     * 查询最大的考试id
     * @return
     */
    @Select("select max(exam_id) from exam")
    Integer selectMaxExamId();
}
