package com.csp.app.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.csp.app.common.ResponseBuilder;
import com.csp.app.entity.Score;
import com.csp.app.service.ScoreService;
import com.csp.app.util.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author chengsp on 2019年1月14日12:00:42
 */
@RequestMapping("/score")
@Controller
public class ScoreController {
    @Autowired
    private ScoreService scoreService;

    @RequestMapping("/add")
    @ResponseBody
    public boolean add(Score Score) {
        return scoreService.insert(Score);
    }

    @RequestMapping("/queryByEntity")
    @ResponseBody
    public Page<Score> queryByEntity(Score Score, int current, int size) {
        return scoreService.selectPage(new Page<>(current, size), new EntityWrapper<>(Score));
    }

    @RequestMapping("/queryAll")
    @ResponseBody
    public String queryAll() {
        List<Score> scores = scoreService.selectList(new EntityWrapper<>(null));
        return ResponseBuilder.buildSuccess("成功", scores).toString();
    }

}
