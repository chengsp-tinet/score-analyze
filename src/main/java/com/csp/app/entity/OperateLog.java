package com.csp.app.entity;

import com.csp.app.common.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

/**
 * 操作日志实体
 *
 * @author chengsp on 2019年4月4日17:03:45
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class OperateLog extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "JDBC")
    private Integer id;
    @Column
    private String username;
    @Column
    private String interfaceUrl;
    @Column
    private String method;
    @Column
    private String param;
    @Column
    private Integer status;
    @Column
    private Integer description;
    @Column
    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getInterfaceUrl() {
        return interfaceUrl;
    }

    public void setInterfaceUrl(String interfaceUrl) {
        this.interfaceUrl = interfaceUrl;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getDescription() {
        return description;
    }

    public void setDescription(Integer description) {
        this.description = description;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
