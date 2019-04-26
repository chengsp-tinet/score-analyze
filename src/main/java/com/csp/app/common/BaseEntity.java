package com.csp.app.common;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;
import java.util.Comparator;

public abstract class BaseEntity implements Serializable {
    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
