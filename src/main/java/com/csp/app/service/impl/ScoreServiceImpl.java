package com.csp.app.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.csp.app.common.CacheKey;
import com.csp.app.entity.Clasz;
import com.csp.app.entity.Course;
import com.csp.app.entity.Exam;
import com.csp.app.entity.Score;
import com.csp.app.entity.Student;
import com.csp.app.mapper.ScoreMapper;
import com.csp.app.mapper.SystemSettingMapper;
import com.csp.app.service.ClassService;
import com.csp.app.service.CourseService;
import com.csp.app.service.ExamService;
import com.csp.app.service.RedisService;
import com.csp.app.service.ScoreService;
import com.csp.app.service.StudentService;
import com.csp.app.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.util.StringUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author chengsp
 */
@Service
public class ScoreServiceImpl extends ServiceImpl<ScoreMapper, Score> implements ScoreService {
    private final static Logger logger = LoggerFactory.getLogger(ScoreServiceImpl.class);
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

    @Override
    public boolean add(Score score) {
        completeEntity(score);
        return insert(score);
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
            Exam exam = examService.getEntityFromLocalCacheByKey(String.format(CacheKey.EXAM_NAME_EXAM
                    , score.getExamName()));
            if (exam != null) {
                examId = exam.getExamId();
            }
        }
        if (examName == null) {
            Exam exam = examService.getEntityFromLocalCacheByKey(String.format(CacheKey.EXAM_ID_EXAM, score.getExamId()));
            if (exam != null) {
                examName = exam.getExamName();
            }
        }
        if (examId == null || examName == null) {
            throw new RuntimeException("找不到对应的考试信息");
        }
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
        Exam exam = examService.getEntityFromLocalCacheByKey(String.format(CacheKey.EXAM_ID_EXAM, score.getExamId()));
        score.setCourseId(exam.getCourseId());
        score.setCourseName(exam.getCourseName());
    }

    @Override
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
     * @param studentId 学号
     * @return
     */
    @Override
    public List<HashMap<String, Object>> getPersonScores(Integer studentId,Integer examGroupId) {
        List<HashMap<String, Object>> personScores = new ArrayList<>();
        List<Exam> exams = examService.getExamsByGroupId(examGroupId);
        for (Exam exam : exams) {
            HashMap<String,Object> personScore = new HashMap<>();
            Score score = getScoreByStudentAndExamId(studentId, exam.getExamId());
            personScore.put("courseName",score.getCourseName());
            personScore.put("score",score.getScore());
            personScores.add(personScore);
        }
        return personScores;
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
     * @param studentId
     * @param examId
     * @return
     */
    private Score getScoreByStudentAndExamId(Integer studentId,Integer examId){
        EntityWrapper entityWrapper = new EntityWrapper(new Score());
        entityWrapper.eq("student_id",studentId);
        entityWrapper.eq("exam_id",examId);
        return selectOne(entityWrapper);
    }


}
