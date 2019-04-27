package com.csp.app.controller;

import com.csp.app.common.BaseController;
import com.csp.app.common.ResponseBuilder;
import com.csp.app.entity.Clasz;
import com.csp.app.service.ClassService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/class")
public class ClassController  extends BaseController {
    private final static Logger logger = LoggerFactory.getLogger(ClassController.class);
    @Autowired
    private ClassService classService;

    @RequestMapping("/inside/searchAll")
    public ResponseBuilder searchAll() {
        try {
            List<Clasz> claszs = classService.searchAll();
            return ResponseBuilder.buildSuccess("查询成功", claszs);
        } catch (Exception e) {
            logger.error("searchAll error: {}", e);
            return ResponseBuilder.buildError("系统异常:" + e.getMessage());
        }
    }

    @RequestMapping(value = "/inside/add", method = RequestMethod.POST)
    public ResponseBuilder add(Clasz clasz) {
        try {
            if (clasz.getClassNum() == null || clasz.getToSchoolYear() == null || clasz.getType() == null) {
                return ResponseBuilder.buildSuccess("添加班级失败,classNum/toSchoollYear/type不可为空", null);
            }
            boolean result = classService.add(clasz);
            if (result) {
                return ResponseBuilder.buildSuccess("添加班级成功", clasz);
            } else {
                return ResponseBuilder.buildSuccess("添加班级失败", null);
            }
        } catch (Exception e) {
            logger.error("add error: {}", e);
            return ResponseBuilder.buildError("系统异常:" + e.getMessage());
        }
    }
}
