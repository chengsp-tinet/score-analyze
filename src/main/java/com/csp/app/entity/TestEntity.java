package com.csp.app.entity;

import com.csp.app.common.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.Date;

/**
 * @author chengsp on 2019年3月24日17:40:23
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "JDBC")
    @Column
    private Integer id;
    @Column
    private String clid;

    @Column
    private String detailClid;

    @Column
    private String customerNumber;
    @Column
    private String customerProvince;

    @Column
    private String customerCity;

    @Column
    private String calleeNumber;

    @Column
    private String calleeAreaCode;

    @Column
    private Long requestTime;

    @Column
    private Long startTime;

    @Column
    private Long answerTime;

    @Column
    private Long dialTime;

    @Column
    private Long bridgeTime;

    @Column
    private Long endTime;

    @Column
    private Integer monitorDuration;

    @Column
    private Integer billDuration;

    @Column
    private Integer bridgeDuration;

    @Column
    private Integer totalDuration;

    @Column
    private Integer callType;

    @Column
    private Integer status;

    @Column
    private Integer mark;

    @Column
    private Integer endReason;

    @Column
    private String userField;

    @Column
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public String getClid() {
        return clid;
    }

    public void setClid(String clid) {
        this.clid = clid;
    }

    public String getDetailClid() {
        return detailClid;
    }

    public void setDetailClid(String detailClid) {
        this.detailClid = detailClid;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }


    public String getCustomerProvince() {
        return customerProvince;
    }

    public void setCustomerProvince(String customerProvince) {
        this.customerProvince = customerProvince;
    }

    public String getCustomerCity() {
        return customerCity;
    }

    public void setCustomerCity(String customerCity) {
        this.customerCity = customerCity;
    }

    public String getCalleeNumber() {
        return calleeNumber;
    }

    public void setCalleeNumber(String calleeNumber) {
        this.calleeNumber = calleeNumber;
    }

    public String getCalleeAreaCode() {
        return calleeAreaCode;
    }

    public void setCalleeAreaCode(String calleeAreaCode) {
        this.calleeAreaCode = calleeAreaCode;
    }

    public Long getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Long requestTime) {
        this.requestTime = requestTime;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getAnswerTime() {
        return answerTime;
    }

    public void setAnswerTime(Long answerTime) {
        this.answerTime = answerTime;
    }

    public Long getDialTime() {
        return dialTime;
    }

    public void setDialTime(Long dialTime) {
        this.dialTime = dialTime;
    }

    public Long getBridgeTime() {
        return bridgeTime;
    }

    public void setBridgeTime(Long bridgeTime) {
        this.bridgeTime = bridgeTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Integer getMonitorDuration() {
        return monitorDuration;
    }

    public void setMonitorDuration(Integer monitorDuration) {
        this.monitorDuration = monitorDuration;
    }

    public Integer getBillDuration() {
        return billDuration;
    }

    public void setBillDuration(Integer billDuration) {
        this.billDuration = billDuration;
    }

    public Integer getBridgeDuration() {
        return bridgeDuration;
    }

    public void setBridgeDuration(Integer bridgeDuration) {
        this.bridgeDuration = bridgeDuration;
    }

    public Integer getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(Integer totalDuration) {
        this.totalDuration = totalDuration;
    }

    public Integer getCallType() {
        return callType;
    }

    public void setCallType(Integer callType) {
        this.callType = callType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getMark() {
        return mark;
    }

    public void setMark(Integer mark) {
        this.mark = mark;
    }

    public Integer getEndReason() {
        return endReason;
    }

    public void setEndReason(Integer endReason) {
        this.endReason = endReason;
    }


    public String getUserField() {
        return userField;
    }

    public void setUserField(String userField) {
        this.userField = userField;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
