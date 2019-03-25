package com.csp.app.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.csp.app.entity.TestEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TestMapper extends BaseMapper<TestEntity> {
    @Select("select * from cdr limit 500")
    public List<TestEntity> selectPart();

}
