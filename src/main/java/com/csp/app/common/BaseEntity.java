package com.csp.app.common;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;
import java.util.Comparator;

public abstract class BaseEntity implements Serializable, Comparator {
    private Integer id;
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
