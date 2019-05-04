package com.csp.app.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.csp.app.common.BaseController;
import com.csp.app.common.Const;
import com.csp.app.common.ResponseBuilder;
import com.csp.app.entity.Course;
import com.csp.app.service.CourseService;
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
@RequestMapping("/course")
public class CourseController extends BaseController {
    private final static Logger logger = LoggerFactory.getLogger(CourseController.class);
    @Autowired
    private CourseService courseService;

    @RequestMapping("/inside/search")
    public ResponseBuilder search(String courseName,String courseId) {
        try {
            EntityWrapper<Course> wrapper = new EntityWrapper<>();
            if (StringUtil.isNotEmpty(courseName)) {
                wrapper.like("course_name", "%" + courseName + "%");
            }
            if (courseId != null) {
                wrapper.eq("course_id", courseId);
            }
            wrapper.orderBy("create_time", false);
            List<Course> courseList = courseService.selectList(wrapper);
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

    @RequestMapping(value = "/inside/addBatch",method = RequestMethod.POST)
    @ResponseBody
    public ResponseBuilder addBatch(@RequestParam("file") MultipartFile file) {
        InputStream is = null;
        List<Course> courses = null;
        try {
            is = file.getInputStream();
            courses = ExcelUtil.read(is, 0, file.getOriginalFilename(), null, Course.class);
            courseService.batchAdd(courses);
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
