package com.csp.app.entity;

import com.csp.app.common.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Score extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "JDBC")
    private Integer id;
    /**
     * 学生
     */
    @Column
    private Long studentId;
    @Column
    private String studentName;
    /**
     * 班级
     */
    @Column
    private Integer classId;

    /**
     * 年级
     */
    @Column
    private Integer gradeNum;
    /**
     * 分数
     */
    @Column
    private Double score;
    /**
     * 考试唯一标识
     */
    @Column
    private Integer examId;
    /**
     * 考试全名
     */
    @Column
    private String examName;
    /**
     * 考试组id
     */
    @Column
    private Integer examGroupId;
    /**
     * 考试组名
     */
    @Column
    private String examGroupName;
    /**
     * 科目id
     */
    @Column
    private Integer courseId;
    /**
     * 科目名称
     */
    @Column
    private String courseName;
    @Column
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    public Integer getGradeNum() {
        return gradeNum;
    }

    public void setGradeNum(Integer gradeNum) {
        this.gradeNum = gradeNum;
    }

    public Double getScore() {
        return score;
    }

    public Integer getExamId() {
        return examId;
    }

    public Integer getExamGroupId() {
        return examGroupId;
    }

    public void setExamGroupId(Integer examGroupId) {
        this.examGroupId = examGroupId;
    }

    public String getExamGroupName() {
        return examGroupName;
    }

    public void setExamGroupName(String examGroupName) {
        this.examGroupName = examGroupName;
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

    public void setScore(Double score) {
        this.score = score;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
