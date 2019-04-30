package com.csp.app.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.service.IService;
import com.csp.app.common.CacheService;
import com.csp.app.entity.Score;
import org.springframework.cache.annotation.Cacheable;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 1.	个人成绩单：要求展示各科目成绩，班级科目名次，年级科目名次，总分，班级总分名次，年级总分名次
 * 2.	班级成绩单：要求展示某个班级所有同学各科目成绩，班级科目名次，年级科目名次，总分，班级总分名次，年级总分名次，科目平均分，年级科目平均分名次，并且可以选择其中某一个名次进行排序，科目成绩标准差，总分标准差，优率，良率，优良率，合格率，不合格率
 * 3.	年级成绩单：要求展示某个年级所有同学各科目成绩，班级科目名次，年级科目名次，总分，班级总分名次，年级总分名次，科目平均分，学校科目平均分名次，科目成绩标准差，总分标准差，优率，良率，优良率，合格率，不合格率
 * 4.	班级科目分数段分布
 * 5.	年级科目分数段分布
 * 6.	成绩录入，登陆，注册，成绩导出
 */
public interface ScoreService extends IService<Score>{
    /**
     * 添加成绩记录
     * @param score
     * @return
     */
    boolean add(Score score);

    /**
     * 批量录入成绩
     * @param scores
     * @return
     */
    boolean batchAdd(List<Score> scores);


    /**
     * 个人成绩单
     * @param studentId 学号
     * @return
     */
    JSONObject getPersonScores(Integer studentId, Integer examGroupId);


    /**
     * 查询年级总分排名
     * @param examGroupId
     * @return
     */
    Map<Object, Integer> searchTotalScoreGradeOrderMap(Integer examGroupId);

    /**
     * 查询班级总分排名
     * @param examGroupId
     * @param classId
     * @return
     */
    Map<Object, Integer> searchTotalScoreClassOrderMap(Integer examGroupId, Integer classId);

    /**
     * 班级成绩单
     * @param classId 班级编号
     * @return
     */
    List<HashMap<String,Object>> getClazzScore(Integer classId);

    /**
     * 年纪成绩单
     * @param gradeNum 年级编号
     * @return
     */
    List<HashMap<String,Object>> getGradeScore(Integer gradeNum);

    /**
     * 查询单科班级排名
     * @param examId
     * @param classId
     * @return
     */
    Map<Object, Integer> getClassScoreOrderMap(Integer examId, Integer classId);
    /**
     * 查询单个年级排名
     * @param examId
     * @return
     */
    Map<Object, Integer> getGradeScoreOrderMap(Integer examId);
}
