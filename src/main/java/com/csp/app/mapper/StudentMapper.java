package com.csp.app.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.csp.app.entity.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface StudentMapper extends BaseMapper<Student> {
    /**
     * 查询一个班的最大的学号
     * @param classId
     * @return
     */
    @Select("select max(student_id) from student where class_id = #{classId}")
    Long selectMaxStudentIdByClassId(@Param("classId") Integer classId);
}
