package com.csp.app.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.csp.app.common.BaseController;
import com.csp.app.common.ResponseBuilder;
import com.csp.app.entity.Student;
import com.csp.app.service.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentController extends BaseController {
    @Autowired
    private StudentService studentService;
    private final static Logger logger = LoggerFactory.getLogger(StudentController.class);

    @RequestMapping(value = "/inside/add", method = RequestMethod.POST)
    public ResponseBuilder add(Student student) {
        try {
            if (student.getClassId() == null || student.getStudentName() == null) {
                return ResponseBuilder.buildFail("添加学生失败,班级id/学生姓名不可为空");
            }
            boolean result = studentService.add(student);
            if (result) {
                return ResponseBuilder.buildSuccess("添加学生成功", student);
            } else {
                return ResponseBuilder.buildFail("添加学生失败");
            }
        } catch (Exception e) {
            logger.error("add student error:{}", e);
            return ResponseBuilder.buildError("添加学生失败,系统异常" + e.toString());
        }
    }

    @RequestMapping("/inside/searchSelectivePage")
    public ResponseBuilder searchSelective(Student student) {
        EntityWrapper<Student> wrapper = new EntityWrapper<>(student);
        List<Student> students = studentService.selectList(wrapper);
        return ResponseBuilder.buildSuccess("查询成功", students);
    }
}
