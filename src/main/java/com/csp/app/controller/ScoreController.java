package com.csp.app.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.csp.app.common.BaseController;
import com.csp.app.common.CacheKey;
import com.csp.app.common.ResponseBuilder;
import com.csp.app.entity.Course;
import com.csp.app.entity.Exam;
import com.csp.app.entity.Score;
import com.csp.app.entity.Student;
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
@RequestMapping("/inside/score")
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
            Double scoreValue = score.getScore();
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
    public ResponseBuilder searchSelectivePage(Score score, Integer page, Integer limit
            , String orderFiled, String orderType, Integer ltScore, Integer geScore) {
        try {
            if (page == null) {
                page = 1;
            }
            if (limit == null) {
                limit = 10;
            }
            EntityWrapper<Score> wrapper = new EntityWrapper<>(score);
            String studentName = score.getStudentName();
            String examName = score.getExamName();
            String examGroupName = score.getExamGroupName();
            score.setStudentName(null);
            score.setStudentName(null);
            score.setExamName(null);
            score.setExamGroupName(null);
            if (StringUtil.isNotEmpty(studentName)) {
                wrapper.like("student_name", "%" + studentName + "%");
            }
            if (StringUtil.isNotEmpty(examName)) {
                wrapper.like("exam_name", "%" + studentName + "%");
            }
            if (StringUtil.isNotEmpty(examGroupName)) {
                wrapper.like("exam_group_name", "%" + studentName + "%");
            }
            if (ltScore != null) {
                wrapper.lt("score", ltScore);
            }
            if (geScore != null) {
                wrapper.ge("score", geScore);
            }
            int count = scoreService.selectCount(wrapper);
            boolean orderTypeBoo = false;
            if (StringUtil.isNotEmpty(orderType) && orderType.equals("asc")) {
                orderTypeBoo = true;
            }
            if (StringUtil.isEmpty(orderFiled)) {
                orderFiled = "id";
            }
            wrapper.orderBy(orderFiled, orderTypeBoo);
            Page<Score> scorePage = new Page<>(page, limit);
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

    @RequestMapping(value = "/inside/addBatch", method = RequestMethod.POST)
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
    public ResponseBuilder searchPersonalScore(Student student, Integer examGroupId, Integer page, Integer limit
            , String orderField, String orderType) {
        try {
            if (examGroupId == null) {
                return ResponseBuilder.buildFail("失败,考试组号不可为空");
            }
            Page personScores = scoreService.getPersonScores(student, examGroupId, page, limit, orderField, orderType);

            return ResponseBuilder.buildPage("成功", personScores.getRecords(), (int) personScores.getTotal());
        } catch (Exception e) {
            logger.error("searchPersonalScore error :{}", e);
            return ResponseBuilder.buildFail("失败,系统异常:" + e.getMessage());
        }
    }

    @RequestMapping("/inside/searchClassScore")
    @ResponseBody
    public ResponseBuilder searchClassScore(Integer classId, Integer examGroupId) {
        try {
            if (classId == null) {
                return ResponseBuilder.buildFail("失败,班级编号不可为空");
            }
            if (examGroupId == null) {
                return ResponseBuilder.buildFail("失败,考试组号不可为空");
            }
            JSONObject classScore = scoreService.getClassScore(classId, examGroupId);
            return ResponseBuilder.buildSuccess("成功", classScore);
        } catch (Exception e) {
            logger.error("searchClassScore error :{}", e);
            return ResponseBuilder.buildFail("失败,系统异常:" + e.getMessage());
        }
    }

    @RequestMapping("/inside/searchGradeScore")
    @ResponseBody
    public ResponseBuilder searchGradeScore(Integer examGroupId) {
        try {
            if (examGroupId == null) {
                return ResponseBuilder.buildFail("失败,考试组号不可为空");
            }
            JSONArray gradeScore = scoreService.getGradeScore(examGroupId);
            return ResponseBuilder.buildSuccess("成功", gradeScore);
        } catch (Exception e) {
            logger.error("searchGradeScore error :{}", e);
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
            logger.error("updateSelective error :{}", e);
            return ResponseBuilder.buildFail("失败,系统异常:" + e.getMessage());
        }
    }

    @RequestMapping(value = "/inside/getScoreShowTemplate")
    @ResponseBody
    public ResponseBuilder getScoreShowTemplate(Integer examGroupId) {
        try {
            if (examGroupId == null) {
                return ResponseBuilder.buildFail("失败,考试组号不可为空");
            }
            JSONArray scoreShowTemplate = scoreService.getScoreShowTemplate(examGroupId);
            return ResponseBuilder.buildSuccess("成功", scoreShowTemplate);
        } catch (Exception e) {
            logger.error("getScoreShowTemplate error :{}", e);
            return ResponseBuilder.buildError("失败,系统异常:" + e.getMessage());
        }
    }

    @RequestMapping(value = "/inside/analyzeScale")
    @ResponseBody
    public ResponseBuilder analyzeScale(Integer examGroupId, Integer classId, Integer courseId, Integer minScore
            , Integer maxScore, Integer granularity) {
        try {
            if (examGroupId == null || courseId == null) {
                return ResponseBuilder.buildFail("失败,考试组号/科目编号不可为空");
            }
            List list = scoreService.analyzeCourseScoreScale(examGroupId, classId, courseId, minScore, maxScore, granularity);
            return ResponseBuilder.buildSuccess("成功", list);
        } catch (Exception e) {
            logger.error("analyzeScale error :{}", e);
            return ResponseBuilder.buildError("失败,系统异常:" + e.getMessage());
        }
    }

    @RequestMapping(value = "/inside/analyzeTotalScoreScale")
    @ResponseBody
    public ResponseBuilder analyzeTotalScoreScale(Integer examGroupId, Integer classId, Integer minScore
            , Integer maxScore, Integer granularity) {
        try {
            List list = scoreService.analyzeTotalScoreScale(examGroupId, classId, minScore, maxScore, granularity);
            return ResponseBuilder.buildSuccess("成功", list);
        } catch (Exception e) {
            logger.error("analyzeTotalScoreScale error :{}", e);
            return ResponseBuilder.buildError("失败,系统异常:" + e.getMessage());
        }
    }

}
