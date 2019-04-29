package com.csp.app.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.csp.app.entity.Clasz;
import com.csp.app.entity.ExamGroup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ExamGroupMapper extends BaseMapper<ExamGroup> {
    /**
     * 查询最大的考试id
     * @return
     */
    @Select("select max(exam_group_id) from exam")
    Integer selectMaxExamGroupId();
}
