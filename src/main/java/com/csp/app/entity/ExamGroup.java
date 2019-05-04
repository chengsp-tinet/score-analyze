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

/**
 * @author chengsp on 2019年4月28日18:20:47
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExamGroup extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "JDBC")
    private Integer id;

    @Column
    private Integer examGroupId;
    @Column
    private String examGroupName;
    @Column
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
