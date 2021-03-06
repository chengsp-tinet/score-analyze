package com.csp.app.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.csp.app.common.BaseController;
import com.csp.app.common.ResponseBuilder;
import com.csp.app.entity.Exam;
import com.csp.app.service.ExamService;
import com.csp.app.util.ExcelUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.util.StringUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/inside/exam")
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

    @RequestMapping("/inside/searchSelectivePage")
    public ResponseBuilder searchSelectivePage(Exam exam, Integer page, Integer limit,String examName,String courseName) {
        try {
            exam.setExamName(null);
            exam.setCourseName(null);
            EntityWrapper<Exam> wrapper = new EntityWrapper<>(exam);
            if (StringUtil.isNotEmpty(examName)) {
                wrapper.like("exam_name","%" + examName + "%");
            }
            if (StringUtil.isNotEmpty(courseName)) {
                wrapper.like("course_name","%" + courseName + "%");
            }
            int count = examService.selectCount(wrapper);
            Page<Exam> examPage = new Page<>(page,limit,"id",false);
            examService.selectPage(examPage, wrapper);
            return ResponseBuilder.buildPage("查询成功", examPage.getRecords(), count);
        } catch (Exception e) {
            logger.error("searchSelectivePage error: {}", e);
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
            return ResponseBuilder.buildError("系统异常:" + e.getMessage());
        }
    }

    @RequestMapping(value = "/inside/addBatch" ,method = RequestMethod.POST)
    @ResponseBody
    public ResponseBuilder addBatch(@RequestParam("file") MultipartFile file) {
        InputStream is = null;
        List<Exam> exams = null;
        try {
            is = file.getInputStream();
            exams = ExcelUtil.read(is, 0, file.getOriginalFilename(), null, Exam.class);
            examService.batchAdd(exams);
            logger.info("导入成功!");
            return ResponseBuilder.buildSuccess("成功", null);
        } catch (Exception e) {
            logger.error("导入发生异常:{}", e);
            return ResponseBuilder.buildError("批量导入成绩失败,系统异常" + e.getMessage());
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
