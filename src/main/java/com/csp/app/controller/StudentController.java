package com.csp.app.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.csp.app.common.BaseController;
import com.csp.app.common.ResponseBuilder;
import com.csp.app.entity.Student;
import com.csp.app.service.StudentService;
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

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/inside/student")
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

    @RequestMapping(value = "/inside/addBatch",method = RequestMethod.POST)
    @ResponseBody
    public ResponseBuilder addBatch(@RequestParam("file") MultipartFile file) {
        InputStream is = null;
        List<Student> students = null;
        try {
            is = file.getInputStream();
            students = ExcelUtil.read(is, 0, file.getOriginalFilename(), null, Student.class);
            studentService.batchAdd(students);
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

    @RequestMapping("/inside/searchSelectivePage")
    public ResponseBuilder searchSelectivePage(Student student, Integer page, Integer limit, String studentName
            , String orderFiled, String orderType) {
        try {
            Page studentPage = studentService.searchSelectivePage(student, page, limit, studentName, orderFiled, orderType);
            return ResponseBuilder.buildPage("查询成功",studentPage.getRecords() , (int) studentPage.getTotal());
        } catch (Exception e) {
            logger.error("searchSelectivePage error: {}", e);
            return ResponseBuilder.buildError("系统异常:" + e.getMessage());
        }
    }

    @RequestMapping("/inside/export")
    public void export(Student student, HttpServletResponse response) {
        studentService.export(student,response);
    }
}
