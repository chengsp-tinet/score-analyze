package com.csp.app.controller;

import com.csp.app.common.BaseController;
import com.csp.app.common.ResponseBuilder;
import com.csp.app.entity.Admin;
import com.csp.app.service.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.util.StringUtil;

import javax.servlet.http.HttpSession;

@RestController
public class AdminController extends BaseController {
    private final static Logger logger = LoggerFactory.getLogger(AdminController.class);
    @Autowired
    private AdminService adminService;

    @RequestMapping("/login")
    public ResponseBuilder login(Admin admin, HttpSession session) {
        try {
            Admin checkUser = adminService.login(admin);
            if (StringUtil.isEmpty(admin.getAdminName())||StringUtil.isEmpty(admin.getPassword())) {
                return ResponseBuilder.buildFail("失败,账号或密码不可为空");
            }
            if (checkUser == null) {
                return ResponseBuilder.buildFail("失败,密码或账号错误");
            } else {
                session.setAttribute("user",checkUser);
                return ResponseBuilder.buildSuccess(checkUser);
            }
        } catch (Exception e) {
            logger.error("login error: {}",e);
            return ResponseBuilder.buildError("失败,系统异常:"+e.getMessage());
        }
    }
}
