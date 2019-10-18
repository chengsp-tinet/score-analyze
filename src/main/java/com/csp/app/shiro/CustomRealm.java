package com.csp.app.shiro;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.csp.app.entity.Admin;
import com.csp.app.service.AdminService;
import com.csp.app.util.ContextUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chengsp
 * @date 2019/10/18 10:50
 */
public class CustomRealm extends AuthorizingRealm {
    @Override
    /**
     * 认证
     */
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        //1.获取用户输入的账号
        String username = (String) token.getPrincipal();
        //2.通过username从数据库中查找到user实体
        Admin user = getUserByUserName(username);
        if (user == null) {
            return null;
        }
        //3.通过SimpleAuthenticationInfo做身份处理
        SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(user, user.getPassword(),
                getName());
        //5.返回身份处理对象
        return simpleAuthenticationInfo;
    }

    @Override
    /**
     * 授权
     */
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principal) {
        return null;
    }

    /**
     * 模拟通过username从数据库中查找到user实体
     *
     * @param username
     * @return
     */
    private Admin getUserByUserName(String username) {
        List<Admin> users = ContextUtil.getApplicationContext().getBean(AdminService.class).
                selectList(new EntityWrapper<>(new Admin.Builder().adminName(username).build()));
        for (Admin user : users) {
            if (user.getAdminName().equals(username)) {
                return user;
            }
        }
        return null;
    }

}
