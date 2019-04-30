package com.csp.app.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.csp.app.common.CacheKey;
import com.csp.app.entity.Clasz;
import com.csp.app.entity.Course;
import com.csp.app.entity.Exam;
import com.csp.app.entity.ExamGroup;
import com.csp.app.entity.Score;
import com.csp.app.entity.Student;
import com.csp.app.mapper.ScoreMapper;
import com.csp.app.mapper.SystemSettingMapper;
import com.csp.app.service.ClassService;
import com.csp.app.service.CourseService;
import com.csp.app.service.ExamGroupService;
import com.csp.app.service.ExamService;
import com.csp.app.service.RedisService;
import com.csp.app.service.ScoreService;
import com.csp.app.service.StudentService;
import com.csp.app.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.util.StringUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chengsp
 */
@Service
public class ScoreServiceImpl extends ServiceImpl<ScoreMapper, Score> implements ScoreService {
    private final static Logger logger = LoggerFactory.getLogger(ScoreServiceImpl.class);
    private static DecimalFormat decimalFormat = new DecimalFormat("0.00");
    @Autowired
    private SystemSettingMapper systemSettingMapper;
    @Autowired
    private RedisService redisService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private ClassService classService;
    @Autowired
    private ExamService examService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private ScoreMapper scoreMapper;
    @Autowired
    private ExamGroupService examGroupService;

    @CacheEvict(value = {"getPersonScores","searchTotalScoreGradeOrderMap","searchTotalScoreClassOrderMap"
            ,"getClassScoreOrderMap","getGradeScoreOrderMap"},allEntries = true)
    @Override
    public boolean add(Score score) {
        completeEntity(score);
        return insert(score);
    }

    @CacheEvict(value = {"getPersonScores","searchTotalScoreGradeOrderMap","searchTotalScoreClassOrderMap"
            ,"getClassScoreOrderMap","getGradeScoreOrderMap"},allEntries = true)
    @Override
    public boolean updateById(Score entity) {
        return super.updateById(entity);
    }
    private void completeEntity(Score score) {
        String studentName = null;
        Integer studentId = score.getStudentId();
        Student student = studentService.getEntityFromLocalCacheByKey(String.format(CacheKey.STUDENT_ID_STUDENT
                , score.getStudentId()));
        if (student != null) {
            studentName = student.getStudentName();
        }
        if (StringUtil.isEmpty(studentName)) {
            throw new RuntimeException("找不到该学生,学号:" + studentId);
        }
        score.setStudentName(studentName);
        Integer examId = score.getExamId();
        String examName = score.getExamName();
        if (examId == null) {
            Exam exam = examService.getEntityFromLocalCacheByKey(String.format(CacheKey.EXAM_NAME_EXAM, score.getExamName()));
            if (exam != null) {
                examId = exam.getExamId();
            }
        }
        String key = String.format(CacheKey.EXAM_ID_EXAM, score.getExamId());
        if (examName == null) {
            Exam exam = examService.getEntityFromLocalCacheByKey(key);
            if (exam != null) {
                examName = exam.getExamName();
            }
        }
        if (examId == null || examName == null) {
            throw new RuntimeException("找不到对应的考试信息");
        }

        Exam exam = examService.getEntityFromLocalCacheByKey(key);
        if (exam == null) {
            throw new RuntimeException("找不到对应的考试信息");
        }
        score.setExamGroupId(exam.getExamGroupId());
        score.setExamGroupName(exam.getExamGroupName());
        score.setExamId(examId);
        score.setExamName(examName);
        score.setClassId(student.getClassId());
        Clasz clasz = classService.getEntityFromLocalCacheByKey(String.format(CacheKey.CLASS_ID_CLASS
                , score.getClassId()));
        Integer toSchoolYear = clasz.getToSchoolYear();
        Calendar instance = Calendar.getInstance();
        instance.setTime(new Date());
        int nowYear = instance.get(Calendar.YEAR);
        Integer gradeNum = nowYear - toSchoolYear;
        // 开学时间
        String toSchoolTime = DateUtil.format(new Date(), "yyyy-09-01 00:00:00");
        String nowTime = DateUtil.format(instance.getTime(), DateUtil.FMT_DATE_YYYY_MM_DD_HH_mm_ss);
        if (nowTime.compareTo(toSchoolTime) > 0) {
            gradeNum++;
        }
        score.setGradeNum(gradeNum);
        score.setCourseId(exam.getCourseId());
        score.setCourseName(exam.getCourseName());
    }

    @Override
    @CacheEvict(value = {"getPersonScores","searchTotalScoreGradeOrderMap","searchTotalScoreClassOrderMap"
            ,"getClassScoreOrderMap","getGradeScoreOrderMap"},allEntries = true)
    public boolean batchAdd(List<Score> scores) {
        if (CollectionUtils.isNotEmpty(scores)) {
            for (Score score : scores) {
                completeEntity(score);
            }
            return insertBatch(scores);
        }
        return true;
    }

    /**
     * 各科目成绩，班级科目名次，年级科目名次，总分，班级总分名次，年级总分名次
     *
     * @param studentId 学号
     * @return
     */
    @Override
    @Cacheable("getPersonScores")
    public JSONObject getPersonScores(Integer studentId, Integer examGroupId) {
        JSONObject scoreMsg = new JSONObject();
        List<Map> personScores = new ArrayList<>();
        ExamGroup examGroup = examGroupService.getEntityFromLocalCacheByKey(String.format(CacheKey.EXAM_GROUP_ID_EXAM_GROUP, examGroupId));
        if (examGroup == null) {
            throw new RuntimeException("考试组不存在,考试组id:" + examGroupId);
        }
        Student student = studentService.getEntityFromLocalCacheByKey(String.format(CacheKey.STUDENT_ID_STUDENT, studentId));
        if (student == null) {
            throw new RuntimeException("没有这样的学生,学号:" + studentId);
        }
        List<Exam> exams = examService.getExamsByGroupId(examGroupId);
        if (exams.isEmpty()) {
            throw new RuntimeException("考试组未添加考试,考试组id:" + examGroupId);
        }
        Score score = null;
        int totalScore = 0;
        for (Exam exam : exams) {
            JSONObject personScore = new JSONObject();
            Map<Object, Integer> classOrderMap = getClassScoreOrderMap(exam.getExamId(), student.getClassId());
            Map<Object, Integer> gradeOrderMap = getGradeScoreOrderMap(exam.getExamId());
            score = getScoreByStudentAndExamId(studentId, exam.getExamId());
            Integer scoreValue = 0;
            if (score == null) {
                personScore.put("score", scoreValue);
            } else {
                scoreValue = score.getScore();
                personScore.put("score", scoreValue);
                totalScore += scoreValue;
            }
            Integer courseId = exam.getCourseId();
            Course course = courseService.getEntityFromLocalCacheByKey(String.format(CacheKey.COURSE_ID_COURSE
                    , courseId));
            boolean isPass = true;
            if (course == null) {
                throw new RuntimeException("没有这样的科目,科目id" + courseId);
            }
            if (scoreValue < course.getFullScore() * 0.6) {
                isPass = false;
            }
            personScore.put("courseName", exam.getCourseName());
            personScore.put("isPass", isPass);
            personScore.put("courseId", courseId);
            personScore.put("fullScore", course.getFullScore());
            personScore.put("examName", exam.getExamName());
            personScore.put("examId", exam.getExamId());
            personScore.put("classOrder", classOrderMap.get(studentId));
            personScore.put("gradeOrder", gradeOrderMap.get(studentId));
            personScores.add(personScore);
        }
        scoreMsg.put("personScores", personScores);
        Map<Object, Integer> gradeOrderMap = searchTotalScoreGradeOrderMap(examGroupId);
        Map<Object, Integer> classOrderMap = searchTotalScoreClassOrderMap(examGroupId, student.getClassId());
        scoreMsg.put("gradeOrder", gradeOrderMap.get(studentId.toString()));
        scoreMsg.put("studentId", studentId);
        scoreMsg.put("classId", student.getClassId());
        scoreMsg.put("studentName", student.getStudentName());
        scoreMsg.put("classOrder", classOrderMap.get(studentId.toString()));
        scoreMsg.put("examGroupId", examGroup.getExamGroupId());
        scoreMsg.put("examGroupName", examGroup.getExamGroupName());
        scoreMsg.put("gradeNum", score.getGradeNum());
        scoreMsg.put("totalScore", totalScore);
        scoreMsg.put("averageScore", decimalFormat.format(totalScore * 1.0 / exams.size()));
        return scoreMsg;
    }

    @Override
    @Cacheable("searchTotalScoreGradeOrderMap")
    public Map<Object, Integer> searchTotalScoreGradeOrderMap(Integer examGroupId) {
        List<Map> gradeOrderInfoMaps = scoreMapper.searchTotalScoreGradeOrder(examGroupId);
        Map<Object, Integer> gradeOrderMap = new LinkedHashMap<>();
        int i = 1;
        for (Map map : gradeOrderInfoMaps) {
            gradeOrderMap.put(map.get("studentId").toString(), i++);
        }
        return gradeOrderMap;
    }

    @Override
    @Cacheable("searchTotalScoreClassOrderMap")
    public Map<Object, Integer> searchTotalScoreClassOrderMap(Integer examGroupId, Integer classId) {
        List<Map> gradeOrderInfoMaps = scoreMapper.searchTotalScoreClassOrder(examGroupId, classId);
        Map<Object, Integer> classOrderMap = new LinkedHashMap<>();
        int i = 1;
        for (Map map : gradeOrderInfoMaps) {
            classOrderMap.put(map.get("studentId").toString(), i++);
        }
        return classOrderMap;
    }

    @Override
    public List<HashMap<String, Object>> getClazzScore(Integer classId) {
        return null;
    }

    @Override
    public List<HashMap<String, Object>> getGradeScore(Integer gradeNum) {
        return null;
    }

    /**
     * 通过学号和考试组查询考试
     *
     * @param studentId
     * @param examId
     * @return
     */
    private Score getScoreByStudentAndExamId(Integer studentId, Integer examId) {
        EntityWrapper<Score> entityWrapper = new EntityWrapper<>(new Score());
        entityWrapper.eq("student_id", studentId);
        entityWrapper.eq("exam_id", examId);
        return selectOne(entityWrapper);
    }

    @Override
    @Cacheable("getClassScoreOrderMap")
    public Map<Object, Integer> getClassScoreOrderMap(Integer examId, Integer classId) {
        LinkedHashMap<Object, Integer> orderMap = new LinkedHashMap<>();
        EntityWrapper<Score> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("exam_id", examId);
        entityWrapper.eq("class_id", classId);
        entityWrapper.orderBy("score", false);
        List<Score> scores = selectList(entityWrapper);
        int i = 1;
        for (Score score : scores) {
            orderMap.put(score.getStudentId(), i++);
        }
        return orderMap;
    }


    @Override
    @Cacheable("getGradeScoreOrderMap")
    public Map<Object, Integer> getGradeScoreOrderMap(Integer examId) {
        LinkedHashMap<Object, Integer> orderMap = new LinkedHashMap<>();
        EntityWrapper<Score> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("exam_id", examId);
        entityWrapper.orderBy("score", false);
        List<Score> scores = selectList(entityWrapper);
        int i = 1;
        for (Score score : scores) {
            orderMap.put(score.getStudentId(), i++);
        }
        return orderMap;
    }

}
