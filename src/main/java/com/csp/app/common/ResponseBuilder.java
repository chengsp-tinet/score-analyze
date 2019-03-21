package com.csp.app.common;

import com.alibaba.fastjson.JSON;

public class ResponseBuilder {
    public static final int SUCCESS_STATUS = 0;
    public static final int FAIL_STATUS = 1;
    public static final int EROOR_STATUS = -1;

    private int status;
    private String description;
    private Object data;

    public ResponseBuilder() {
    }

    public ResponseBuilder(int status, String description, Object data) {
        this.status = status;
        this.description = description;
        this.data = data;
    }

    public static ResponseBuilder buildSuccess(String description, Object data) {
        return new ResponseBuilder(SUCCESS_STATUS, description, data);
    }

    public static ResponseBuilder buildFail(String description) {
        return new ResponseBuilder(FAIL_STATUS, description, null);
    }

    public static ResponseBuilder buildError() {
        return new ResponseBuilder(EROOR_STATUS, "系统异常", null);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
