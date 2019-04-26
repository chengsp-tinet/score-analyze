package com.csp.app.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.csp.app.common.ResponseBuilder;
import com.csp.app.entity.Clasz;
import com.csp.app.service.ClassService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/inside/class")
public class ClassController {
    private final static Logger logger = LoggerFactory.getLogger(ClassController.class);
    @Autowired
    private ClassService classService;

    @RequestMapping("/searchAll")
    public ResponseBuilder searchAll() {
        try {
            List<Clasz> claszs = classService.searchAll();
            return ResponseBuilder.buildSuccess("查询成功", claszs);
        } catch (Exception e) {
            logger.error("searchAll error: {}", e);
            return ResponseBuilder.buildSuccess("系统异常:" + e.getMessage(), null);
        }
    }
}
