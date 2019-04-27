package com.csp.app.controller;

import com.csp.app.common.BaseController;
import com.csp.app.common.Const;
import com.csp.app.common.ResponseBuilder;
import com.csp.app.entity.Clasz;
import com.csp.app.entity.Course;
import com.csp.app.service.CourseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/course")
public class CourseController extends BaseController {
    private final static Logger logger = LoggerFactory.getLogger(CourseController.class);
    @Autowired
    private CourseService courseService;

    @RequestMapping("/inside/searchAll")
    public ResponseBuilder searchAll() {
        try {
            List<Course> courseList = courseService.searchAll();
            return ResponseBuilder.buildSuccess("查询成功", courseList);
        } catch (Exception e) {
            logger.error("searchAll error: {}", e);
            return ResponseBuilder.buildError("系统异常:" + e.getMessage());
        }
    }

    @RequestMapping(value = "/inside/add", method = RequestMethod.POST)
    public ResponseBuilder add(Course course) {
        try {
            if (course.getCourseName() == null) {
                return ResponseBuilder.buildFail("添加课程失败,课程名不可为空");
            }
            if (course.getFullScore() == null) {
                course.setFullScore(Const.DEFAULT_FULL_SCORE);
            }
            boolean result = courseService.insert(course);
            if (result) {
                return ResponseBuilder.buildSuccess("添加课程成功", course);
            } else {
                return ResponseBuilder.buildFail("添加课程失败");
            }
        } catch (Exception e) {
            logger.error("add error: {}", e);
            return ResponseBuilder.buildError("系统异常:" + e.getMessage());
        }
    }
}
