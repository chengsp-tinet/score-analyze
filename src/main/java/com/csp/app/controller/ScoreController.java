package com.csp.app.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.csp.app.common.BaseController;
import com.csp.app.common.ResponseBuilder;
import com.csp.app.entity.Score;
import com.csp.app.service.ScoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author chengsp on 2019年1月14日12:00:42
 */
@RequestMapping("/score")
@Controller
public class ScoreController extends BaseController {
    private final static Logger logger = LoggerFactory.getLogger(ScoreController.class);
    @Autowired
    private ScoreService scoreService;

    @RequestMapping(value = "/inside/add", method = RequestMethod.POST)
    @ResponseBody
    public ResponseBuilder add(Score score) {
        try {
            if (score.getStudentId() == null) {
                return ResponseBuilder.buildFail("添加成绩失败,学号不能为空");
            }
            if (score.getExamId() == null && score.getExamName() == null) {
                return ResponseBuilder.buildFail("添加成绩失败,考试编号和考试名称不可全为空");
            }
            if (score.getScore() == null) {
                return ResponseBuilder.buildFail("添加成绩失败,成绩不可为空");
            }
            boolean result = scoreService.add(score);
            if (result) {
                return ResponseBuilder.buildSuccess("添加成绩成功", score);
            } else {
                return ResponseBuilder.buildFail("添加成绩失败");
            }
        } catch (Exception e) {
            logger.error("insert score error: {}", e);
            return ResponseBuilder.buildError("添加异常:" + e.getMessage());
        }
    }

    @RequestMapping("/inside/searchSelective")
    @ResponseBody
    public Page<Score> queryByEntity(Score Score, int current, int size) {
        return scoreService.selectPage(new Page<>(current, size), new EntityWrapper<>(Score));
    }

    @RequestMapping("/inside/searchAll")
    @ResponseBody
    public ResponseBuilder queryAll() {
        List<Score> scores = scoreService.selectList(new EntityWrapper<>(null));
        return ResponseBuilder.buildSuccess("成功", scores);
    }
}
