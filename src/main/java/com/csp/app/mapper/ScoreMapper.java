package com.csp.app.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.csp.app.entity.Score;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface ScoreMapper extends BaseMapper<Score> {
    /**
     * 查询所有学生科目总分年级排名
     *
     * @param examGroupId
     * @return
     */
    @Select("SELECT sum(score) total,student_id studentId FROM score WHERE exam_group_id =#{examGroupId} GROUP BY student_id ORDER BY total DESC")
    List<Map> searchTotalScoreGradeOrder(@Param("examGroupId") Integer examGroupId);

    /**
     * 查询所有学生科目总分班级排名
     *
     * @param examGroupId
     * @param classId
     * @return
     */
    @Select("SELECT sum(score) total,student_id studentId FROM score WHERE exam_group_id =#{examGroupId} and class_id=#{classId} GROUP BY student_id ORDER BY total DESC")
    List<Map> searchTotalScoreClassOrder(@Param("examGroupId") Integer examGroupId, @Param("classId") Integer classId);
}
