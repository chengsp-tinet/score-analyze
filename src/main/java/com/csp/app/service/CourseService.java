package com.csp.app.service;

import com.baomidou.mybatisplus.service.IService;
import com.csp.app.common.CacheService;
import com.csp.app.entity.Course;

import java.util.List;

public interface CourseService extends IService<Course> , CacheService<Course> {
    /**
     * 查询所有课程
     * @return
     */
    List<Course> searchAll();
}
