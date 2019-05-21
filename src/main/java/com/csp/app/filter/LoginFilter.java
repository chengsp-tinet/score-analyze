package com.csp.app.filter;

import com.csp.app.common.ResponseBuilder;
import com.csp.app.entity.Admin;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@WebFilter(urlPatterns = {"/inside/*"})
public class LoginFilter implements Filter {

    private static String encoding = "UTF-8";
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpSession session = request.getSession();
        request.setCharacterEncoding(encoding);
        servletResponse.setCharacterEncoding(encoding);
        Admin user = (Admin) session.getAttribute("user");
        if (user == null) {
            PrintWriter writer = servletResponse.getWriter();
            writer.println(ResponseBuilder.buildFail("请先登录!").toString());
        } else {
            filterChain.doFilter(servletRequest,servletResponse);
        }
    }

    @Override
    public void destroy() {

    }
}
