package com.csp.app.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.csp.app.common.BaseController;
import com.csp.app.common.ResponseBuilder;
import com.csp.app.entity.Clasz;
import com.csp.app.service.ClaszService;
import com.csp.app.util.ExcelUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/inside/class")
public class ClassController  extends BaseController {
    private final static Logger logger = LoggerFactory.getLogger(ClassController.class);
    @Autowired
    private ClaszService claszService;

    @RequestMapping("/inside/searchAll")
    public ResponseBuilder searchAll() {
        try {
            List<Clasz> claszs = claszService.searchAll();
            return ResponseBuilder.buildSuccess("查询成功", claszs);
        } catch (Exception e) {
            logger.error("searchAll error: {}", e);
            return ResponseBuilder.buildError("系统异常:" + e.getMessage());
        }
    }
    @RequestMapping("/inside/search")
    public ResponseBuilder search(Clasz clasz) {
        try {
            EntityWrapper<Clasz> wrapper = new EntityWrapper<>(clasz);
            List<Clasz> claszs = claszService.selectList(wrapper);
            return ResponseBuilder.buildSuccess("查询成功", claszs);
        } catch (Exception e) {
            logger.error("search error: {}", e);
            return ResponseBuilder.buildError("系统异常:" + e.getMessage());
        }
    }
    @RequestMapping(value = "/inside/add", method = RequestMethod.POST)
    public ResponseBuilder add(Clasz clasz, Map map) {
        try {
            if (clasz.getClassNum() == null || clasz.getToSchoolYear() == null || clasz.getType() == null) {
                return ResponseBuilder.buildSuccess("添加班级失败,classNum/toSchoolYear/type不可为空", null);
            }
            boolean result = claszService.add(clasz);
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

    @RequestMapping(value = "/inside/addBatch",method = RequestMethod.POST)
    @ResponseBody
    public ResponseBuilder addBatch(@RequestParam("file") MultipartFile file) {
        InputStream is = null;
        List<Clasz> claszs = null;
        try {
            is = file.getInputStream();
            claszs = ExcelUtil.read(is, 0, file.getOriginalFilename(), null, Clasz.class);
            claszService.batchAdd(claszs);
            logger.info("导入成功!");
            return ResponseBuilder.buildSuccess("成功", null);
        } catch (Exception e) {
            logger.error("导入发生异常:{}", e);
            return ResponseBuilder.buildError("批量导入成绩失败,系统异常" + e.getMessage());
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
