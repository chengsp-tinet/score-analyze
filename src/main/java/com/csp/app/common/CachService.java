package com.csp.app.common;

/**
 * @author chengsp 2019年3月23日11:39:57
 */
public interface CachService<T> {
    /** 缓存数据库数据
     * @param t
     */
    void redisLoad(T t);
}
