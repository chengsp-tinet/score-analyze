package com.csp.app.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.csp.app.entity.Cdr;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CdrMapper extends BaseMapper<Cdr> {
    @Select("select * from cdr limit 500")
    public List<Cdr> selectPart();

}
