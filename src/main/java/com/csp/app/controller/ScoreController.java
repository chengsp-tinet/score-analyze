package com.csp.app.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.csp.app.common.BaseController;
import com.csp.app.common.CacheKey;
import com.csp.app.common.ResponseBuilder;
import com.csp.app.entity.Course;
import com.csp.app.entity.Exam;
import com.csp.app.entity.Score;
import com.csp.app.service.CourseService;
import com.csp.app.service.ExamService;
import com.csp.app.service.ScoreService;
import com.csp.app.util.ExcelUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.util.StringUtil;

import java.io.IOException;
import java.io.InputStream;
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
    @Autowired
    private CourseService courseService;
    @Autowired
    private ExamService examService;

    @RequestMapping(value = "/inside/add", method = RequestMethod.POST)
    @ResponseBody
    public ResponseBuilder add(Score score) {
        try {
            if (score.getStudentId() == null) {
                return ResponseBuilder.buildFail("添加成绩失败,学号不能为空");
            }
            Integer examId = score.getExamId();
            if (examId == null && score.getExamName() == null) {
                return ResponseBuilder.buildFail("添加成绩失败,考试编号和考试名称不可全为空");
            }
            Integer scoreValue = score.getScore();
            if (scoreValue == null) {
                return ResponseBuilder.buildFail("添加成绩失败,成绩不可为空");
            }
            Exam exam = examService.getEntityFromLocalCacheByKey(String.format(CacheKey.EXAM_ID_EXAM, examId));
            Course course = courseService.getEntityFromLocalCacheByKey(String.format(CacheKey.COURSE_ID_COURSE
                    , exam.getCourseId()));
            if (scoreValue > course.getFullScore()) {
                return ResponseBuilder.buildFail("添加成绩失败,成绩不合法,大于满分:" + scoreValue);
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

    @RequestMapping("/inside/searchSelectivePage")
    @ResponseBody
    public ResponseBuilder searchSelectivePage(Score score, Integer page, Integer limit, String studentName
            , String orderFiled, String orderType) {
        try {
            if (page == null) {
                page = 0;
            }
            if (limit == null) {
                limit = 10;
            }
            score.setStudentName(null);
            EntityWrapper<Score> wrapper = new EntityWrapper<>(score);
            if (StringUtil.isNotEmpty(studentName)) {
                wrapper.like("student_name", "%" + studentName + "%");
            }
            int count = scoreService.selectCount(wrapper);
            boolean orderTypeBoo = true;
            if (StringUtil.isNotEmpty(orderType) && orderType.equals("asc")) {
                orderTypeBoo = false;
            }
            if (StringUtil.isEmpty(orderFiled)) {
                orderFiled = "id";
            }
            Page<Score> scorePage = new Page<>(page, limit, orderFiled, orderTypeBoo);
            scorePage.setTotal(count);
            scoreService.selectPage(scorePage, wrapper);
            return ResponseBuilder.buildPage("查询成功", scorePage.getRecords(), count);
        } catch (Exception e) {
            logger.error("searchSelectivePage error: {}", e);
            return ResponseBuilder.buildError("系统异常:" + e.getMessage());
        }
    }

    @RequestMapping("/inside/searchAll")
    @ResponseBody
    public ResponseBuilder searchAll() {
        List<Score> scores = scoreService.selectList(new EntityWrapper<>(null));
        return ResponseBuilder.buildSuccess("成功", scores);
    }

    @RequestMapping(value = "/inside/addBatch",method = RequestMethod.POST)
    @ResponseBody
    public ResponseBuilder addBatch(@RequestParam("file") MultipartFile file) {
        InputStream is = null;
        List<Score> scoreList = null;
        try {
            is = file.getInputStream();
            scoreList = ExcelUtil.read(is, 0, file.getOriginalFilename(), null, Score.class);
            scoreService.batchAdd(scoreList);
            logger.info("导入成功!");
            return ResponseBuilder.buildSuccess("成功", null);
        } catch (Exception e) {
            logger.error("导入发生异常 :{}", e);
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

    @RequestMapping("/inside/searchPersonalScore")
    @ResponseBody
    public ResponseBuilder searchPersonalScore(Integer studentId, Integer examGroupId) {
        try {
            if (studentId == null) {
                return ResponseBuilder.buildFail("失败,学号不可为空");
            }
            if (examGroupId == null) {
                return ResponseBuilder.buildFail("失败,考试组号不可为空");
            }
            JSONObject personScores = scoreService.getPersonScores(studentId, examGroupId);
            return ResponseBuilder.buildSuccess("成功", personScores);
        } catch (Exception e) {
            logger.error("searchPersonalScore error :{}", e);
            return ResponseBuilder.buildFail("失败,系统异常:" + e.getMessage());
        }
    }

    @RequestMapping(value = "/inside/updateSelective", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseBuilder updateSelective(Score score) {
        try {
            scoreService.updateById(score);
            return ResponseBuilder.buildSuccess("成功", null);
        } catch (Exception e) {
            logger.error("searchPersonalScore error :{}", e);
            return ResponseBuilder.buildFail("失败,系统异常:" + e.getMessage());
        }
    }

}
