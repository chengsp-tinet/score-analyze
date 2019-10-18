package com.csp.app.controller;

import com.csp.app.common.BaseController;
import com.csp.app.common.ResponseBuilder;
import com.csp.app.entity.Admin;
import com.csp.app.service.AdminService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
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

    @RequestMapping("/login")
    public ResponseBuilder login(Admin admin, HttpSession session) {
        if (StringUtil.isEmpty(admin.getAdminName()) || StringUtil.isEmpty(admin.getPassword())) {
            return ResponseBuilder.buildFail("失败,账号或密码不可为空");
        }
        UsernamePasswordToken token = new UsernamePasswordToken(admin.getAdminName(), admin.getPassword());
        Subject currentUser = SecurityUtils.getSubject();

        try {
            //主体提交登录请求到SecurityManager
            currentUser.login(token);
        } catch (IncorrectCredentialsException ice) {
            return ResponseBuilder.buildFail("失败,密码或账号错误");
        } catch (UnknownAccountException uae) {
            return ResponseBuilder.buildFail("失败,密码或账号错误");
        } catch (AuthenticationException ae) {
            logger.error("状态不正确");
        }
        if (currentUser.isAuthenticated()) {
            return ResponseBuilder.buildSuccess(currentUser.getPrincipal());
        } else {
            token.clear();
        }
        return ResponseBuilder.buildFail("未知异常");
    }
}
