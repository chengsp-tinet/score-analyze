package com.csp.app.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.csp.app.entity.Course;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CourseMapper extends BaseMapper<Course> {
    /**
     * 查询最大的课程id
     * @return
     */
    @Select("select max(course_id) from course")
    Integer selectMaxCourseId();
}
