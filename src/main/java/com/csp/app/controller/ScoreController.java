package com.csp.app.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
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
@RequestMapping("/inside/score")
@Controller
public class ScoreController {
    private final static Logger logger = LoggerFactory.getLogger(ScoreController.class);
    @Autowired
    private ScoreService scoreService;

    @RequestMapping(value = "/addOne", method = RequestMethod.POST)
    @ResponseBody
    public ResponseBuilder addOne(Score score) {
        try {
            if (score.getStudentId() == null) {
                return ResponseBuilder.buildFail("添加成绩失败,学号不能为空");
            }
            boolean b = scoreService.insertAllColumn(score);
            if (b) {
                return ResponseBuilder.buildSuccess("添加成绩成功", score);
            } else {
                return ResponseBuilder.buildFail("添加成绩失败");
            }
        } catch (Exception e) {
            logger.error("insert score error: {}", e);
            return ResponseBuilder.buildError("添加异常:" + e.getMessage());
        }
    }

    @RequestMapping("/searchSelective")
    @ResponseBody
    public Page<Score> queryByEntity(Score Score, int current, int size) {
        return scoreService.selectPage(new Page<>(current, size), new EntityWrapper<>(Score));
    }

    @RequestMapping("/searchAll")
    @ResponseBody
    public ResponseBuilder queryAll() {
        List<Score> scores = scoreService.selectList(new EntityWrapper<>(null));
        return ResponseBuilder.buildSuccess("成功", scores);
    }
}
