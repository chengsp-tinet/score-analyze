package com.csp.app.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
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

import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
        return insert(completeEntity(score));
    }

    private Score completeEntity(Score score) {
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
        Integer gradeNum = nowYear - toSchoolYear + 1;
        score.setGradeNum(gradeNum);
        Exam exam = examService.getEntityFromLocalCacheByKey(String.format(CacheKey.EXAM_ID_EXAM, score.getExamId()));
        score.setCourseId(exam.getCourseId());
        score.setCourseName(exam.getCourseName());
        return score;
    }

    @Override
    public boolean batchAdd(List<Score> scores) {
        for (Score score : scores) {
            completeEntity(score);
        }
        return insertBatch(scores);
    }
}
