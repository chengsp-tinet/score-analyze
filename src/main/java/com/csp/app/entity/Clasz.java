package com.csp.app.entity;

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
public class Clasz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "JDBC")
    private Integer id;
    /**
     * 班级唯一id
     */
    private Integer classId;
    /**
     * 班号,如1代表某年级1班
     */
    @Column
    private Integer classNum;
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getClassNum() {
        return classNum;
    }

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    public void setClassNum(Integer classNum) {
        this.classNum = classNum;
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
