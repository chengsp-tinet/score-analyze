package com.csp.app.entity;

import com.csp.app.common.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Exam extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "JDBC")
    private Integer id;
    /**
     * 考试唯一标识
     */
    @Column
    private Integer examId;
    /**
     * 考试名称
     */
    @Column
    private String examName;
    /**
     * 代表哪个年级的考试
     */
    @Column
    private Integer gradeNum;
    /**
     * 科目
     */
    @Column
    private String courseId;
    @Column
    private String courseName;
    /**
     * 考试开始时间
     */
    @Column
    private Date startTime;
    /**
     * 考试结束时间
     */
    @Column
    private Date endTime;
    /**
     * 考试人数
     */
    @Column
    private Integer studentCount;
    /**
     * 实际到场人数
     */
    @Column
    private Integer actualStudentCount;
    /**
     * 是否完成
     */
    @Column
    private boolean finish;
    @Column
    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getExamId() {
        return examId;
    }

    public void setExamId(Integer examId) {
        this.examId = examId;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public Integer getGradeNum() {
        return gradeNum;
    }

    public void setGradeNum(Integer gradeNum) {
        this.gradeNum = gradeNum;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(Integer studentCount) {
        this.studentCount = studentCount;
    }

    public Integer getActualStudentCount() {
        return actualStudentCount;
    }

    public void setActualStudentCount(Integer actualStudentCount) {
        this.actualStudentCount = actualStudentCount;
    }

    public boolean isFinish() {
        return finish;
    }

    public void setFinish(boolean finish) {
        this.finish = finish;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
