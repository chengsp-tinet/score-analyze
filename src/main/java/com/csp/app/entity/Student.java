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
public class Student extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "JDBC")
    private Integer id;
    /**
     * 学号
     */
    @Column
    private Long studentId;
    /**
     * 学生姓名
     */
    @Column
    private String studentName;
    /**
     * 班级id
     */
    @Column
    private Integer classId;

    /**
     * 入学年份
     */
    @Column
    private Integer toSchoolYear;
    /**
     * 学校类型:1.小学,2.初中
     */
    @Column
    private Integer type;
    @Column
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

    public Student() {
    }

    public Student(Integer classId) {
        this.classId = classId;
    }

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

    public Integer getToSchoolYear() {
        return toSchoolYear;
    }

    public void setToSchoolYear(Integer toSchoolYear) {
        this.toSchoolYear = toSchoolYear;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
