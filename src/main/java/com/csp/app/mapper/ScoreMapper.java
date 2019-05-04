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

    /**
     * 查询班级某课程平均分
     * @param examId
     * @return
     */
    @Select("SELECT avg(score) avg_score,class_id FROM `score` WHERE exam_id={examId} GROUP BY class_id ORDER BY avg_score desc")
    List<Map> selectCourseScoreAvgByExamId(@Param("examId") Integer examId);

    /**
     * 查询班级某课程总分
     * @param examId
     * @return
     */
    @Select("SELECT sum(score) total_score,class_id FROM `score` WHERE exam_id={examId} GROUP BY class_id")
    List<Map> selectCourseScoreTotalByExamId(@Param("examId") Integer examId);
}
