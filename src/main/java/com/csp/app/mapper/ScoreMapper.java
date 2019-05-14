package com.csp.app.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.csp.app.entity.Score;
import com.csp.app.entity.Student;
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
    @Select("select sum(score) total,student_id studentId from score WHERE exam_group_id =#{examGroupId} group by student_id order by total desc")
    List<Map> searchTotalScoreGradeOrder(@Param("examGroupId") Integer examGroupId);

    /**
     * 查询所有学生科目总分班级排名
     *
     * @param examGroupId
     * @param classId
     * @return
     */
    @Select("select sum(score) total,student_id studentId FROM score where exam_group_id =#{examGroupId} and class_id=#{classId} group by student_id order by total desc")
    List<Map> searchTotalScoreClassOrder(@Param("examGroupId") Integer examGroupId, @Param("classId") Integer classId);

    /**
     * 查询班级某课程平均分
     * @param examId
     * @return
     */
    @Select("select avg(score) avg_score,class_id from score where exam_id = #{examId} group by class_id order by avg_score desc")
    List<Map> selectCourseScoreAvgByExamId(@Param("examId") Integer examId);

    /**
     * 查询班级某课程总分
     * @param examId
     * @return
     */
    @Select("select sum(score) total_score,class_id FROM score WHERE exam_id = #{examId} GROUP BY class_id")
    List<Map> selectCourseScoreTotalByExamId(@Param("examId") Integer examId);

    /**
     * 查询班级某考试组某课程平均分和班级人数
     * @param examGroupId
     * @return
     */
    @Select("select sum_score/st_count avg,class_id,st_count from (" +
            "select st.st_count,st.class_id,sc.sum_score from (" +
            "select count(*) st_count,class_id from student group by class_id) st left join (" +
            "select sum(score) sum_score,class_id from score where exam_group_id=#{examGroupId} group by class_id) sc" +
            " on sc.class_id=st.class_id) sum_tab order by avg desc")
    List<Map> selectClassTotalAvg(@Param("examGroupId") Integer examGroupId);

    /**
     * 分页查询参加了某考试组的学生
     * @param student
     * @param start
     * @param limit
     * @param examGroupId
     * @return
     */
    @Select("<script>" +
            " select distinct st.id, st.class_id classId, st.create_time creatTime,st.student_name studentName,st.student_id studentId "+
            ",st.to_school_year toSchoolYear,st.type from  student st left join score sc on st.student_id=sc.student_id "+
            "    <where>" +
            "      <if test='examGroupId != null'>" +
            "        and sc.exam_group_id = #{examGroupId}" +
            "      </if>" +
            "      <if test='student.classId != null'>" +
            "        and st.class_id = #{student.classId}" +
            "      </if>" +
            "      <if test='student.createTime != null'>" +
            "        and st.create_time = #{student.createTime}" +
            "      </if>" +
            "      <if test='student.studentId != null'>" +
            "        and st.student_id = #{student.studentId}" +
            "      </if>" +
            "      <if test='student.studentName != null'>" +
            "        and st.student_name like concat('%',#{student.studentName},'%')" +
            "      </if>" +
            "      <if test='student.toSchoolYear != null'>" +
            "        and st.to_school_year = #{student.toSchoolYear}" +
            "      </if>" +
            "      <if test='student.type != null'>" +
            "        and st.type = #{student.type}" +
            "      </if>" +
            "    </where>" +
            "    order by st.id limit #{start},#{limit}"+
            "</script>")
    List<Student> searchScoreJoinStudentPage(Student student, @Param("start") Integer start
            , @Param("limit") Integer limit, @Param("examGroupId") Integer examGroupId);

    /**
     * 查询加了某考试组的学生个数
     * @param student
     * @param examGroupId
     * @return
     */
    @Select("<script>" +
            " select count(distinct st.id) "+
            " from  student st left join score sc on st.student_id=sc.student_id "+
            "    <where>" +
            "      <if test='examGroupId != null'>" +
            "        and sc.exam_group_id = #{examGroupId}" +
            "      </if>" +
            "      <if test='student.classId != null'>" +
            "        and st.class_id = #{student.classId}" +
            "      </if>" +
            "      <if test='student.createTime != null'>" +
            "        and st.create_time = #{student.createTime}" +
            "      </if>" +
            "      <if test='student.studentId != null'>" +
            "        and st.student_id = #{student.studentId}" +
            "      </if>" +
            "      <if test='student.studentName != null'>" +
            "        and st.student_name like concat('%',#{student.studentName},'%')" +
            "      </if>" +
            "      <if test='student.toSchoolYear != null'>" +
            "        and st.to_school_year = #{student.toSchoolYear}" +
            "      </if>" +
            "      <if test='student.type != null'>" +
            "        and st.type = #{student.type}" +
            "      </if>" +
            "    </where>" +
            "</script>")
    int searchScoreJoinStudentPageCount(Student student,@Param("examGroupId") Integer examGroupId);


    /**
     * 查询班级或者年级某考试某科目分数在某范围内的个数
     * @param examGroupId
     * @param courseId
     * @param classId
     * @param gtScore
     * @param ltScore
     * @return
     */
    @Select("<script> select count(*) from score where exam_group_id=#{examGroupId} and course_id=#{courseId} " +
            "<if test='classId != null'> and class_id=#{classId} </if> " +
            "and score>=#{gtScore} and score<![CDATA[<]]>#{ltScore} </script> ")
    int selectCountBetweenByExamGroupIdAndClassId(@Param("examGroupId") Integer examGroupId, @Param("courseId") Integer courseId
            , @Param("classId") Integer classId, @Param("gtScore") Integer gtScore, @Param("ltScore") Integer ltScore);

    /**
     * 查询班级或者年级某考试总分在某范围内的个数
     * @param examGroupId
     * @param classId
     * @param gtScore
     * @param ltScore
     * @return
     */
    @Select("<script> select count(*) from (select sum(score) total_score  from score where exam_group_id = #{examGroupId} " +
            " <if test='classId != null'> and class_id=#{classId} </if> "+
            "group by student_id) ts where ts.total_score>=#{gtScore} and " +
            "ts.total_score<![CDATA[<]]>#{ltScore} " +
            " </script>")
    int selectTotalScoreCountBetweenByExamGroupIdAndClassId(@Param("examGroupId") Integer examGroupId
            , @Param("classId") Integer classId, @Param("gtScore") Integer gtScore, @Param("ltScore") Integer ltScore);
}
