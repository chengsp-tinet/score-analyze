package com.csp.app.controller.page;

import com.csp.app.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;


/**
 * @author chengsp on 2019年1月14日17:04:18
 */
@Controller
public class PageShowController {
    @RequestMapping("/")
    public String showIndex(HttpSession session) {
        Object user = session.getAttribute("user");
        if (user == null) {
            return "login";
        }
        return "index";
    }
    @RequestMapping("/page/loginPage")
    public String showLogin(HttpSession session) {
        Object user = session.getAttribute("user");
        if (user == null) {
            return "login";
        }
        return "index";
    }

    @RequestMapping("/page/uploadPage")
    public String showUploadFile() {
        return "upload";
    }

    @RequestMapping("/page/showCdr")
    public String showCdr() {
        return "listCdr";
    }

    @RequestMapping("/page/request")
    public String request() {
        return "request";
    }

    @RequestMapping("/page/score/add")
    public String showSoreAdd(Model model,Integer examId) {
        model.addAttribute("examId", examId);
        return "score/add";
    }

    @RequestMapping("/page/exam/add")
    public String showExamAdd(Model model,Integer examGroupId) {
        model.addAttribute("examGroupId", examGroupId);
        return "exam/add";
    }

    @RequestMapping("/page/exam/list")
    public String showExamList() {
        return "exam/list";
    }

    @RequestMapping("/page/class/add")
    public String showClassAdd() {
        return "class/add";
    }

    @RequestMapping("/page/class/list")
    public String showClassList() {
        return "class/list";
    }

    @RequestMapping("/page/student/add")
    public String showStudentAdd(Model model,Integer classId) {
        model.addAttribute("classId", classId);
        return "student/add";
    }

    @RequestMapping("/page/student/list")
    public String showStudentList() {
        return "student/list";
    }

    @RequestMapping("/page/score/list")
    public String showScorePageList() {
        return "score/list";
    }

    @RequestMapping("/page/score/listPersonalScore")
    public String showScorePersonalList() {
        return "score/list-personal-score";
    }
    @RequestMapping("/page/score/listClassScore")
    public String showScoreClassList() {
        return "score/list-class-score";
    }

    @RequestMapping("/page/score/listGradeScore")
    public String showScoreGradeList() {
        return "score/list-grade-score";
    }
    @RequestMapping("/page/score/listScoreScale")
    public String showScoreScaleList() {
        return "score/list-score-scale";
    }
    @RequestMapping("/page/score/listTotalScoreScale")
    public String showTotalScoreScaleList() {
        return "score/list-total-score-scale";
    }

    @RequestMapping("/page/examGroup/add")
    public String showExamGroupAdd() {
        return "examGroup/add";
    }

    @RequestMapping("/page/examGroup/list")
    public String showExamGroupList() {
        return "examGroup/list";
    }

    @RequestMapping("/page/course/add")
    public String showCourseAdd() {
        return "course/add";
    }

    @RequestMapping("/page/course/list")
    public String showCourseList() {
        return "course/list";
    }
}
