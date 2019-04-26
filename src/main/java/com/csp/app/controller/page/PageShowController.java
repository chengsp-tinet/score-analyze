package com.csp.app.controller.page;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author chengsp on 2019年1月14日17:04:18
 */
@RequestMapping("/pageShow")
@Controller
public class PageShowController {

    @RequestMapping("/")
    public String showInde() {
        return "index";
    }

    @RequestMapping("/showUploadFile")
    public String showUploadFile() {
        return "upload";
    }

    @RequestMapping("/showCdr")
    public String showCdr() {
        return "listCdr";
    }

    @RequestMapping("/showLogin")
    public String showLogin(){
        return "login";
    }
    @RequestMapping("/request")
    public String request(){
        return "request";
    }
}
