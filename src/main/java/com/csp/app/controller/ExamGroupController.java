package com.csp.app.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.csp.app.common.BaseController;
import com.csp.app.common.ResponseBuilder;
import com.csp.app.entity.ExamGroup;
import com.csp.app.service.ExamGroupService;
import com.csp.app.util.ExcelUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/examGroup")
public class ExamGroupController extends BaseController {
    private final static Logger logger = LoggerFactory.getLogger(ExamGroupController.class);
    @Autowired
    private ExamGroupService examGroupService;

    @RequestMapping("/inside/searchAll")
    public ResponseBuilder searchAll() {
        try {
            List<ExamGroup> examGroupList = examGroupService.searchAll();
            return ResponseBuilder.buildSuccess("查询成功", examGroupList);
        } catch (Exception e) {
            logger.error("searchAll error: {}", e);
            return ResponseBuilder.buildError("系统异常:" + e.getMessage());
        }
    }

    @RequestMapping("/inside/searchSelectivePage")
    public ResponseBuilder searchSelectivePage(ExamGroup examGroup, Integer page, Integer limit) {
        try {
            EntityWrapper<ExamGroup> wrapper = new EntityWrapper<>(examGroup);
            int count = examGroupService.selectCount(wrapper);
            Page<ExamGroup> conditionPage = new Page<>();
            conditionPage.setCurrent(page);
            conditionPage.setSize(limit);
            Page<ExamGroup> examGroupPage = examGroupService.selectPage(conditionPage, wrapper);
            return ResponseBuilder.buildPage("查询成功", examGroupPage.getRecords(), count);
        } catch (Exception e) {
            logger.error("searchSelectivePage error: {}", e);
            return ResponseBuilder.buildError("系统异常:" + e.getMessage());
        }
    }

    @RequestMapping(value = "/inside/add", method = RequestMethod.POST)
    public ResponseBuilder add(ExamGroup examGroup) {
        try {
            if (examGroup.getExamGroupName() == null) {
                return ResponseBuilder.buildFail("添加考试组失败,名称不可为空");
            }
            boolean result = examGroupService.add(examGroup);
            if (result) {
                return ResponseBuilder.buildSuccess("添加考试组成功", examGroup);
            } else {
                return ResponseBuilder.buildFail("添加考试组失败");
            }
        } catch (Exception e) {
            logger.error("add error: {}", e);
            return ResponseBuilder.buildError("系统异常:" + e.toString());
        }
    }

    @RequestMapping("/inside/addBatch")
    @ResponseBody
    public ResponseBuilder addBatch(@RequestParam("file") MultipartFile file) {
        InputStream is = null;
        List<ExamGroup> examGroups = null;
        try {
            is = file.getInputStream();
            examGroups = ExcelUtil.read(is, 0, file.getOriginalFilename(), null, ExamGroup.class);
            examGroupService.batchAdd(examGroups);
            logger.info("导入成功!");
            return ResponseBuilder.buildSuccess("成功", null);
        } catch (Exception e) {
            logger.error("导入发生异常:{}", e);
            return ResponseBuilder.buildError("批量导入考试组失败," + e.getMessage());
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
