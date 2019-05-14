package com.csp.app.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
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
import com.csp.app.service.ClaszService;
import com.csp.app.service.CourseService;
import com.csp.app.service.ExamGroupService;
import com.csp.app.service.ExamService;
import com.csp.app.service.RedisService;
import com.csp.app.service.ScoreService;
import com.csp.app.service.StudentService;
import com.csp.app.service.SystemSettingService;
import com.csp.app.util.ContextUtil;
import com.csp.app.util.DateUtil;
import org.apache.ibatis.annotations.Param;
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
    private ClaszService claszService;
    @Autowired
    private ExamService examService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private ScoreMapper scoreMapper;
    @Autowired
    private SystemSettingService systemSettingService;
    @Autowired
    private ExamGroupService examGroupService;
    private ScoreService scoreService;

    @CacheEvict(value = {"getClassScore", "selectCourseScoreAvgByExamId", "selectCourseScoreTotalByExamId"
            , "searchCourseScoreAvgOrderMap", "searchCourseScoreTotalMap", "searchTotalScoreGradeOrderMap"
            , "searchTotalScoreClassOrderMap", "getScoreByStudentAndExamId", "getClassScoreOrderMap"
            , "getGradeScoreOrderMap", "getGradeScore"}, allEntries = true)
    @Override
    public boolean add(Score score) {
        completeEntity(score);
        return insert(score);
    }

    @CacheEvict(value = {"getClassScore", "selectCourseScoreAvgByExamId", "selectCourseScoreTotalByExamId"
            , "searchCourseScoreAvgOrderMap", "searchCourseScoreTotalMap", "searchTotalScoreGradeOrderMap"
            , "searchTotalScoreClassOrderMap", "getScoreByStudentAndExamId", "getClassScoreOrderMap"
            , "getGradeScoreOrderMap", "getGradeScore"}, allEntries = true)
    @Override
    public boolean updateById(Score entity) {
        return super.updateById(entity);
    }

    private void completeEntity(Score score) {
        String studentName = null;
        Long studentId = score.getStudentId();
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
        Clasz clasz = claszService.getEntityFromLocalCacheByKey(String.format(CacheKey.CLASS_ID_CLASS
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
    @CacheEvict(value = {"getClassScore", "selectCourseScoreAvgByExamId", "selectCourseScoreTotalByExamId"
            , "searchCourseScoreAvgOrderMap", "searchCourseScoreTotalMap", "searchTotalScoreGradeOrderMap"
            , "searchTotalScoreClassOrderMap", "getScoreByStudentAndExamId", "getClassScoreOrderMap"
            , "getGradeScoreOrderMap", "getGradeScore"}, allEntries = true)
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
     * @param student    学号
     * @param page
     * @param limit
     * @param orderField
     * @param orderType
     * @return
     */
    @Override
    @Cacheable("getPersonScores")
    public Page getPersonScores(Student student, Integer examGroupId, Integer page, Integer limit
            , String orderField, String orderType) {
        JSONArray personScores = new JSONArray();
        ExamGroup examGroup = examGroupService.getEntityFromLocalCacheByKey(String.format(CacheKey.EXAM_GROUP_ID_EXAM_GROUP
                , examGroupId));
        if (examGroup == null) {
            throw new RuntimeException("考试组不存在,考试组id:" + examGroupId);
        }
        Page<Student> studentPage = searchScoreJoinStudentPage(student, page, limit, examGroupId);
        for (Student recorder : studentPage.getRecords()) {
            calculateStudentsScore(recorder, examGroupId, personScores, examGroup);
        }
        personScores.sort((o1, o2) -> {
            JSONObject obj1 = (JSONObject) o1;
            JSONObject obj2 = (JSONObject) o2;
            return obj2.getIntValue("score") - obj1.getIntValue("score");
        });
        Page scorePage = new Page();
        scorePage.setRecords(personScores);
        scorePage.setTotal(studentPage.getTotal());
        return scorePage;
    }

    private void calculateStudentsScore(Student student, Integer examGroupId, JSONArray personScores, ExamGroup examGroup) {
        List<Exam> exams = examService.getExamsByGroupId(examGroupId);
        Long studentId = student.getStudentId();
        if (CollectionUtils.isEmpty(exams)) {
            throw new RuntimeException("考试组未添加考试,考试组id:" + examGroupId);
        }
        Score score = null;
        ScoreService scoreService = getScoreService();
        int totalScore = 0;

        JSONObject jsonObject = new JSONObject();
        for (Exam exam : exams) {
            int examId = exam.getExamId();
            Map<Object, Integer> classOrderMap = scoreService.getClassScoreOrderMap(examId, student.getClassId());
            Map<Object, Integer> gradeOrderMap = scoreService.getGradeScoreOrderMap(examId);
            score = getScoreByStudentAndExamId(studentId, examId);
            Integer scoreValue = 0;
            if (score == null) {
                jsonObject.put("score" + examId, scoreValue);
            } else {
                scoreValue = score.getScore();
                jsonObject.put("score" + examId, scoreValue);
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
            jsonObject.put("courseName" + examId, exam.getCourseName());
            jsonObject.put("isPass" + examId, isPass);
            jsonObject.put("courseId" + examId, courseId);
            jsonObject.put("fullScore" + examId, course.getFullScore());
            jsonObject.put("examName" + examId, exam.getExamName());
            jsonObject.put("examId" + examId, examId);
            jsonObject.put("classOrder" + examId, classOrderMap.get(studentId.toString()));
            jsonObject.put("gradeOrder" + examId, gradeOrderMap.get(studentId.toString()));
        }
        Map<Object, Integer> gradeOrderMap = scoreService.searchTotalScoreGradeOrderMap(examGroupId);
        Map<Object, Integer> classOrderMap = scoreService.searchTotalScoreClassOrderMap(examGroupId, student.getClassId());
        jsonObject.put("gradeOrder", gradeOrderMap.get(studentId.toString()));
        jsonObject.put("courseName", "合计");
        jsonObject.put("studentId", studentId);
        jsonObject.put("classId", student.getClassId());
        jsonObject.put("studentName", student.getStudentName());
        jsonObject.put("classOrder", classOrderMap.get(studentId.toString()));
        jsonObject.put("examGroupId", examGroup.getExamGroupId());
        jsonObject.put("examGroupName", examGroup.getExamGroupName());
        jsonObject.put("gradeNum", score.getGradeNum());
        jsonObject.put("score", totalScore);
        jsonObject.put("averageScore", decimalFormat.format(totalScore * 1.0 / exams.size()));
        personScores.add(jsonObject);
    }

    @Override
    @Cacheable("getClassScore")
    public JSONObject getClassScore(Integer classId, Integer examGroupId) {
        Clasz clasz = claszService.getEntityFromLocalCacheByKey(String.format(CacheKey.CLASS_ID_CLASS, classId));
        if (clasz == null) {
            throw new RuntimeException("不存在这样的班级,id:" + classId);
        }
        ExamGroup examGroup = examGroupService.getEntityFromLocalCacheByKey(String.format(
                CacheKey.EXAM_GROUP_ID_EXAM_GROUP, examGroupId));
        if (examGroup == null) {

            throw new RuntimeException("不存在这样的考试组,id:" + examGroupId);
        }
        JSONObject classScoreMsg = new JSONObject();
        List<Student> students = searchStudentsByClassId(classId);
        ScoreService scoreService = getScoreService();
        List<Exam> exams = examService.getExamsByGroupId(examGroupId);
        JSONArray objects = new JSONArray();
        for (Exam exam : exams) {
            JSONObject jsonObject = new JSONObject();
            Integer examId = exam.getExamId();
            Map<String, Map> orderMap = scoreService.searchCourseScoreAvgOrderMap(examId);
            Map scoreTotalMap = scoreService.searchCourseScoreTotalMap(examId);
            jsonObject.put("examName", exam.getExamName());
            jsonObject.put("examId", examId);
            jsonObject.put("courseId", exam.getCourseId());
            jsonObject.put("courseName", exam.getCourseName());
            Map avgMap = orderMap.get(classId.toString());
            jsonObject.put("courseAvgOrder", avgMap.get("order"));
            jsonObject.put("courseAvg", avgMap.get("avg"));
            jsonObject.put("courseTotalScore", scoreTotalMap.get(classId.toString()));
            Course course = courseService.getEntityFromLocalCacheByKey(String.format(CacheKey.COURSE_ID_COURSE
                    , exam.getCourseId()));
            int passCount = searchCountByExamIdAndClassIdBetweenScore(examId, classId, course.getFullScore() * 0.6
                    , course.getFullScore());
            double passRate = passCount * 1.0 / students.size();
            jsonObject.put("passRate", decimalFormat.format(passRate));
            jsonObject.put("passCount", passCount);
            jsonObject.put("notPassRate", decimalFormat.format(1 - passRate));
            jsonObject.put("notPassCount", students.size() - passCount);
            objects.add(jsonObject);
        }
        classScoreMsg.put("courseAvgOrders", objects);
        return classScoreMsg;
    }

    @Override
    @Cacheable("getGradeScore")
    public JSONArray getGradeScore(Integer examGroupId) {
        JSONArray objects = new JSONArray();
        List<Map> totalAvgs = scoreMapper.selectClassTotalAvg(examGroupId);
        int order = 1;
        Map<String, Integer> orderMap = new HashMap<>();
        for (Map totalAvg : totalAvgs) {
            orderMap.put(totalAvg.get("class_id").toString(), order++);
        }
        for (Map totalAvg : totalAvgs) {
            JSONObject jsonObject = new JSONObject();
            Object classId = totalAvg.get("class_id");
            jsonObject.put("avgOrder", orderMap.get(classId.toString()));
            jsonObject.put("avg", totalAvg.get("avg"));
            jsonObject.put("studentCount", totalAvg.get("st_count"));
            jsonObject.put("classId", classId);
            objects.add(jsonObject);
        }
        return objects;
    }

    @Override
    @Cacheable("selectCourseScoreAvgByExamId")
    public List<Map> selectCourseScoreAvgByExamId(Integer examId) {
        return scoreMapper.selectCourseScoreAvgByExamId(examId);
    }

    /**
     * 查询班级某课程总分
     *
     * @param examId
     * @return
     */
    @Override
    @Cacheable("selectCourseScoreTotalByExamId")
    public List<Map> selectCourseScoreTotalByExamId(@Param("examId") Integer examId) {
        return scoreMapper.selectCourseScoreTotalByExamId(examId);
    }

    @Override
    @Cacheable("searchCourseScoreAvgOrderMap")
    public Map<String, Map> searchCourseScoreAvgOrderMap(Integer examId) {
        ScoreService scoreService = getScoreService();
        List<Map> courseScoreAvgs = scoreService.selectCourseScoreAvgByExamId(examId);
        Map<String, Map> map = new HashMap<>();
        int i = 1;
        for (Map courseScoreAvg : courseScoreAvgs) {
            Map tempMap = new HashMap(2);
            tempMap.put("order", i++);
            tempMap.put("avg", courseScoreAvg.get("avg_score"));
            map.put(courseScoreAvg.get("class_id").toString(), tempMap);
        }
        return map;
    }

    @Override
    @Cacheable("searchCourseScoreTotalMap")
    public Map searchCourseScoreTotalMap(Integer examId) {
        ScoreService scoreService = getScoreService();
        List<Map> courseScoreTotals = scoreService.selectCourseScoreTotalByExamId(examId);
        Map map = new HashMap();
        for (Map courseScoreTotal : courseScoreTotals) {
            map.put(courseScoreTotal.get("class_id").toString(), courseScoreTotal.get("total_score"));
        }
        return map;
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

    /**
     * 通过学号和考试查询对应的成绩
     *
     * @param studentId
     * @param examId
     * @return
     */
    @Override
    @Cacheable("getScoreByStudentAndExamId")
    public Score getScoreByStudentAndExamId(Long studentId, Integer examId) {
        EntityWrapper<Score> entityWrapper = new EntityWrapper<>(null);
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
            orderMap.put(score.getStudentId().toString(), i++);
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
            orderMap.put(score.getStudentId().toString(), i++);
        }
        return orderMap;
    }

    /**
     * 查询某班某考试分数在某范围内的个数
     *
     * @param examId
     * @param classId
     * @param geScore
     * @param ltScore
     * @return
     */
    public int searchCountByExamIdAndClassIdBetweenScore(Integer examId, Integer classId, double geScore, int ltScore) {
        EntityWrapper<Score> wrapper = new EntityWrapper<>();
        wrapper.eq("exam_id", examId);
        wrapper.eq("class_id", classId);
        wrapper.ge("score", geScore);
        wrapper.lt("score", ltScore);
        return scoreMapper.selectCount(wrapper);
    }

    /**
     * 查询某个班级对应的学生
     *
     * @param classId
     * @return
     */
    private List<Student> searchStudentsByClassId(Integer classId) {
        EntityWrapper<Student> wrapper = new EntityWrapper<>(new Student(classId));
        return studentService.selectList(wrapper);
    }

    @Override
    public JSONArray getScoreShowTemplate(Integer examGroupId) {
        ExamGroup examGroup = examGroupService.getEntityFromLocalCacheByKey(String.format(CacheKey.EXAM_GROUP_ID_EXAM_GROUP
                , examGroupId));
        if (examGroup == null) {
            throw new RuntimeException("不存在这样的考试组,id" + examGroupId);
        }
        JSONArray objects = new JSONArray();
        List<Exam> exams = examService.getExamsByGroupId(examGroupId);
        String[] fields = {"studentName", "studentId", "score", "averageScore", "classOrder", "gradeOrder"
                , "examGroupName", "gradeNum", "classId", "examGroupId"};
        String[] fieldName = {"姓名", "学号", "总分", "科目平均分", "班级总分名次", "年级总分名次", "考试组名"
                , "年级", "班级编号", "考试组编号"};
        for (int i = 0; i < fields.length; i++) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("field", fields[i]);
            jsonObject.put("title", fieldName[i]);
            jsonObject.put("align", "center");
            jsonObject.put("width", 120);
            objects.add(jsonObject);
        }
        String[] courseFields = {"courseName", "score", "classOrder", "gradeOrder", "isPass"};
        String[] courseFieldNames = {"科目名", "分数", "班级名次", "年级名次", "是否合格"};
        for (Exam exam : exams) {
            for (int i = 0; i < courseFields.length; i++) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("field", courseFields[i] + exam.getExamId());
                jsonObject.put("title", courseFieldNames[i]);
                jsonObject.put("align", "center");
                jsonObject.put("width", 100);
                objects.add(jsonObject);
            }
        }
        return objects;
    }

    /**
     * 通过bean调用内部方法,使得cache注解生效
     *
     * @return
     */
    private ScoreService getScoreService() {
        if (scoreService == null) {
            scoreService = ContextUtil.getBean(ScoreService.class);
        }
        return scoreService;
    }

    private Page<Student> searchScoreJoinStudentPage(Student student, Integer page, Integer limit, Integer examGroupId) {
        if (page == null) {
            page = 1;
        }
        if (limit == null) {
            limit = 10;
        }
        int start = (page - 1) * limit;
        Page studentPage = new Page(page, limit);
        int count = scoreMapper.searchScoreJoinStudentPageCount(student, examGroupId);
        List<Student> students = scoreMapper.searchScoreJoinStudentPage(student, start, limit, examGroupId);
        studentPage.setRecords(students);
        studentPage.setTotal(count);
        return studentPage;
    }

    @Override
    public List analyzeCourseScoreScale(Integer examGroupId, Integer classId, Integer courseId, Integer minScore, Integer maxScore, Integer granularity) {
        if (minScore == null) {
            minScore = 0;
        }
        if (maxScore == null) {
            maxScore = 100;
        }
        if (granularity == null) {
            granularity = 10;
        }
        List list = new ArrayList();
        Course course = courseService.getEntityFromLocalCacheByKey(String.format(CacheKey.COURSE_ID_COURSE, courseId));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("courseName", course.getCourseName());
        jsonObject.put("courseId", courseId);
        jsonObject.put("examGroupId", examGroupId);
        ExamGroup examGroup = examGroupService.getEntityFromLocalCacheByKey(String.format(CacheKey.EXAM_GROUP_ID_EXAM_GROUP, examGroupId));
        jsonObject.put("examGroupName", examGroup.getExamGroupName());
        int tempScore = minScore;
        while (tempScore < maxScore) {
            int ltScore = tempScore + granularity;
            jsonObject.put("scale" + tempScore + ltScore, scoreMapper.selectCountBetweenByExamGroupIdAndClassId(
                    examGroupId, courseId, classId, tempScore, ltScore));
            tempScore += granularity;
        }
        List templateList = new ArrayList();
        String[] fields = {"courseName", "examGroupName", "examGroupId"};
        String[] fieldName = {"课程名称", "考试组名", "考试组编号"};

        for (int i = 0; i < fields.length; i++) {
            JSONObject tempalteJsonObject = new JSONObject();
            tempalteJsonObject.put("field", fields[i]);
            tempalteJsonObject.put("title", fieldName[i]);
            tempalteJsonObject.put("align", "center");
            tempalteJsonObject.put("width", 120);
            templateList.add(tempalteJsonObject);
        }
        tempScore = minScore;
        while (tempScore < maxScore) {
            JSONObject object = new JSONObject();
            object.put("field", "scale" + tempScore + (tempScore + granularity));
            object.put("title", tempScore + "~" + (tempScore + granularity));
            object.put("align", "center");
            object.put("width", 120);
            tempScore += granularity;
            templateList.add(object);
        }
        list.add(templateList);
        list.add(new Object[]{jsonObject});
        return list;

    }

    @Override
    public List analyzeTotalScoreScale(Integer examGroupId, Integer classId, Integer minScore
            , Integer maxScore, Integer granularity) {
        if (minScore == null) {
            minScore = 0;
        }
        if (maxScore == null) {
            maxScore = 300;
        }
        if (granularity == null) {
            granularity = 10;
        }
        List list = new ArrayList();
        ExamGroup examGroup = examGroupService.getEntityFromLocalCacheByKey(String.format(CacheKey.EXAM_GROUP_ID_EXAM_GROUP, examGroupId));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("examGroupId", examGroupId);
        jsonObject.put("examGroupName", examGroup.getExamGroupName());
        int tempScore = minScore;
        while (tempScore < maxScore) {
            int ltScore = tempScore + granularity;
            int count = scoreMapper.selectTotalScoreCountBetweenByExamGroupIdAndClassId(examGroupId, classId, tempScore
                    , ltScore);
            jsonObject.put("scale" + tempScore + ltScore, count);
            tempScore += granularity;
        }

        List templateList = new ArrayList();
        String[] fields = {"examGroupName", "examGroupId"};
        String[] fieldName = {"考试组名", "考试组编号"};

        for (int i = 0; i < fields.length; i++) {
            JSONObject tempalteJsonObject = new JSONObject();
            tempalteJsonObject.put("field", fields[i]);
            tempalteJsonObject.put("title", fieldName[i]);
            tempalteJsonObject.put("align", "center");
            tempalteJsonObject.put("width", 120);
            templateList.add(tempalteJsonObject);
        }
        tempScore = minScore;
        while (tempScore < maxScore) {
            JSONObject object = new JSONObject();
            object.put("field", "scale" + tempScore + (tempScore + granularity));
            object.put("title", tempScore + "~" + (tempScore + granularity));
            object.put("align", "center");
            object.put("width", 120);
            tempScore += granularity;
            templateList.add(object);
        }
        list.add(templateList);
        list.add(new Object[]{jsonObject});
        return list;
    }
}
