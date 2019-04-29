package com.csp.app.controller.page;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author chengsp on 2019年1月14日17:04:18
 */
@RequestMapping("/page")
@Controller
public class PageShowController {

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
    public String showLogin(){
        return "login";
    }

    @RequestMapping("/request")
    public String request(){
        return "request";
    }

    @RequestMapping("/score/add")
    public String showSoreAdd(){
        return "score/add";
    }

    @RequestMapping("/exam/add")
    public String showExamAdd(){
        return "exam/add";
    }

    @RequestMapping("/exam/list")
    public String showExamList(){
        return "exam/list";
    }

    @RequestMapping("/class/add")
    public String showClassAdd(){
        return "class/add";
    }

    @RequestMapping("/class/list")
    public String showClassList(){
        return "class/list";
    }

    @RequestMapping("/student/add")
    public String showStudentAdd(){
        return "student/add";
    }
    @RequestMapping("/student/list")
    public String showStudentList(){
        return "student/list";
    }

    @RequestMapping("/examGroup/add")
    public String showExamGroupAdd(){
        return "examGroup/add";
    }

    @RequestMapping("/examGroup/list")
    public String showExamGroupList(){
        return "examGroup/list";
    }
}
