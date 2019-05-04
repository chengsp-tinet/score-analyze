package com.csp.app.controller.page;

import com.csp.app.entity.Exam;
import com.csp.app.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author chengsp on 2019年1月14日17:04:18
 */
@RequestMapping("/page")
@Controller
public class PageShowController {
    @Autowired
    private ExamService examService;

    @RequestMapping("/")
    public String showIndex() {
        return "index";
    }

    @RequestMapping("/uploadPage")
    public String showUploadFile() {
        return "upload";
    }

    @RequestMapping("/showCdr")
    public String showCdr() {
        return "listCdr";
    }

    @RequestMapping("/loginPage")
    public String showLogin() {
        return "login";
    }

    @RequestMapping("/request")
    public String request() {
        return "request";
    }

    @RequestMapping("/score/add")
    public String showSoreAdd(Model model) {
        List<Exam> exams = examService.searchAll();
        model.addAttribute("exams", exams);
        return "score/add";
    }

    @RequestMapping("/exam/add")
    public String showExamAdd() {
        return "exam/add";
    }

    @RequestMapping("/exam/list")
    public String showExamList() {
        return "exam/list";
    }

    @RequestMapping("/class/add")
    public String showClassAdd() {
        return "class/add";
    }

    @RequestMapping("/class/list")
    public String showClassList() {
        return "class/list";
    }

    @RequestMapping("/student/add")
    public String showStudentAdd() {
        return "student/add";
    }

    @RequestMapping("/student/list")
    public String showStudentList() {
        return "student/list";
    }

    @RequestMapping("/score/list")
    public String showScorePageList() {
        return "score/list";
    }

    @RequestMapping("/score/listPersonalScore")
    public String showScorePersonalList() {
        return "score/list-personal-score";
    }
    @RequestMapping("/score/listClassScore")
    public String showScoreClassList() {
        return "score/list-class-score";
    }

    @RequestMapping("/examGroup/add")
    public String showExamGroupAdd() {
        return "examGroup/add";
    }

    @RequestMapping("/examGroup/list")
    public String showExamGroupList() {
        return "examGroup/list";
    }

    @RequestMapping("/course/add")
    public String showCourseAdd() {
        return "course/add";
    }

    @RequestMapping("/course/list")
    public String showCourseList() {
        return "course/list";
    }
}
