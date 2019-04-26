package com.csp.app.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.csp.app.entity.Student;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StudentMapper extends BaseMapper<Student> {

}
