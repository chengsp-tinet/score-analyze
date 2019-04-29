package com.csp.app.entity;

import java.io.Serializable;
import java.util.Date;

public class OperateLog implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column operate_log.id
     *
     * @mbg.generated
     */
    private Integer id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column operate_log.create_time
     *
     * @mbg.generated
     */
    private Date createTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column operate_log.description
     *
     * @mbg.generated
     */
    private Integer description;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column operate_log.interface_url
     *
     * @mbg.generated
     */
    private String interfaceUrl;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column operate_log.method
     *
     * @mbg.generated
     */
    private String method;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column operate_log.param
     *
     * @mbg.generated
     */
    private String param;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column operate_log.status
     *
     * @mbg.generated
     */
    private Integer status;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column operate_log.username
     *
     * @mbg.generated
     */
    private String username;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table operate_log
     *
     * @mbg.generated
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operate_log.id
     *
     * @return the value of operate_log.id
     *
     * @mbg.generated
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operate_log.id
     *
     * @param id the value for operate_log.id
     *
     * @mbg.generated
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operate_log.create_time
     *
     * @return the value of operate_log.create_time
     *
     * @mbg.generated
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operate_log.create_time
     *
     * @param createTime the value for operate_log.create_time
     *
     * @mbg.generated
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operate_log.description
     *
     * @return the value of operate_log.description
     *
     * @mbg.generated
     */
    public Integer getDescription() {
        return description;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operate_log.description
     *
     * @param description the value for operate_log.description
     *
     * @mbg.generated
     */
    public void setDescription(Integer description) {
        this.description = description;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operate_log.interface_url
     *
     * @return the value of operate_log.interface_url
     *
     * @mbg.generated
     */
    public String getInterfaceUrl() {
        return interfaceUrl;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operate_log.interface_url
     *
     * @param interfaceUrl the value for operate_log.interface_url
     *
     * @mbg.generated
     */
    public void setInterfaceUrl(String interfaceUrl) {
        this.interfaceUrl = interfaceUrl;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operate_log.method
     *
     * @return the value of operate_log.method
     *
     * @mbg.generated
     */
    public String getMethod() {
        return method;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operate_log.method
     *
     * @param method the value for operate_log.method
     *
     * @mbg.generated
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operate_log.param
     *
     * @return the value of operate_log.param
     *
     * @mbg.generated
     */
    public String getParam() {
        return param;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operate_log.param
     *
     * @param param the value for operate_log.param
     *
     * @mbg.generated
     */
    public void setParam(String param) {
        this.param = param;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operate_log.status
     *
     * @return the value of operate_log.status
     *
     * @mbg.generated
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operate_log.status
     *
     * @param status the value for operate_log.status
     *
     * @mbg.generated
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operate_log.username
     *
     * @return the value of operate_log.username
     *
     * @mbg.generated
     */
    public String getUsername() {
        return username;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operate_log.username
     *
     * @param username the value for operate_log.username
     *
     * @mbg.generated
     */
    public void setUsername(String username) {
        this.username = username;
    }
}