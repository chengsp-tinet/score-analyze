package com.csp.app.entity;

import com.csp.app.common.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.Date;

/**
 * 系统设置实体类h
 * @author chengsp
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class SystemSetting extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "JDBC")
    private Integer	id;
    @Column
    private String name;
    @Column
    private String	value;
    @Column
    private String remark;
    @Column
    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
