package com.csp.app.controller;

import com.csp.app.common.ResponseBuilder;
import com.csp.app.entity.Course;
import com.csp.app.entity.Exam;
import com.csp.app.service.CourseService;
import com.csp.app.service.ExamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/inside/exam")
public class ExamController {
    private final static Logger logger = LoggerFactory.getLogger(ExamController.class);
    @Autowired
    private ExamService courseService;

    @RequestMapping("/searchAll")
    public ResponseBuilder searchAll() {
        try {
            List<Exam> examList = courseService.searchAll();
            return ResponseBuilder.buildSuccess("查询成功", examList);
        } catch (Exception e) {
            logger.error("searchAll error: {}", e);
            return ResponseBuilder.buildSuccess("系统异常:" + e.getMessage(), null);
        }
    }
}
