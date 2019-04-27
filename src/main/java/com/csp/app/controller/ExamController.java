package com.csp.app.controller;

import com.csp.app.common.BaseController;
import com.csp.app.common.ResponseBuilder;
import com.csp.app.entity.Exam;
import com.csp.app.service.ExamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/exam")
public class ExamController extends BaseController {
    private final static Logger logger = LoggerFactory.getLogger(ExamController.class);
    @Autowired
    private ExamService examService;

    @RequestMapping("/inside/searchAll")
    public ResponseBuilder searchAll() {
        try {
            List<Exam> examList = examService.searchAll();
            return ResponseBuilder.buildSuccess("查询成功", examList);
        } catch (Exception e) {
            logger.error("searchAll error: {}", e);
            return ResponseBuilder.buildError("系统异常:" + e.getMessage());
        }
    }

    @RequestMapping(value = "/inside/add", method = RequestMethod.POST)
    public ResponseBuilder add(Exam exam) {
        try {

            if (exam.getExamName() == null) {
                return ResponseBuilder.buildFail("添加考试失败,考试名称不可为空");
            }
            if (exam.getEndTime() == null || exam.getStartTime() == null || exam.getStudentCount() == null) {
                return ResponseBuilder.buildFail("添加考试失败,开考时间/结束时间/考试人数不可为空");
            }
            if (exam.getCourseId() == null && exam.getCourseName() == null) {
                return ResponseBuilder.buildFail("添加考试失败,课程id和课程名不可全为空");
            }
            boolean result = examService.add(exam);
            if (result) {
                return ResponseBuilder.buildSuccess("添加考试成功", exam);
            } else {
                return ResponseBuilder.buildFail("添加考试失败");
            }
        } catch (Exception e) {
            logger.error("add error: {}", e);
            return ResponseBuilder.buildError("系统异常:" + e.toString());
        }
    }


}
