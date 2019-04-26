package com.csp.app.controller;

import com.csp.app.common.ResponseBuilder;
import com.csp.app.entity.Clasz;
import com.csp.app.entity.Course;
import com.csp.app.service.CourseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/inside/course")
public class CourseController {
    private final static Logger logger = LoggerFactory.getLogger(CourseController.class);
    @Autowired
    private CourseService courseService;

    @RequestMapping("/searchAll")
    public ResponseBuilder searchAll() {
        try {
            List<Course> courseList = courseService.searchAll();
            return ResponseBuilder.buildSuccess("查询成功", courseList);
        } catch (Exception e) {
            logger.error("searchAll error: {}", e);
            return ResponseBuilder.buildSuccess("系统异常:" + e.getMessage(), null);
        }
    }
}
