package com.csp.app.entity;

import com.alibaba.fastjson.JSON;
import com.csp.app.common.FlushType;

public class SynMessage {
    private FlushType flushType;
    private String beanName;
    private Object data;
    private String key;
    public SynMessage() {
    }

    public SynMessage(FlushType flushType, String beanName, Object data, String key) {
        this.flushType = flushType;
        this.beanName = beanName;
        this.data = data;
        this.key = key;
    }

    public FlushType getFlushType() {
        return flushType;
    }

    public void setFlushType(FlushType flushType) {
        this.flushType = flushType;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
