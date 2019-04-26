package com.csp.app.common;

/**
 * 缓存服务
 * @author chengsp 2019年3月23日11:39:57
 */
public interface CacheService<T> {

    /**
     * 缓存所有记录
     */
    default void loadCache() {
    }

    /**
     * 通过key从缓存获取单个实体
     *
     * @param key
     * @return
     */
    default T getEntityFromCacheByKey(String key) {
        return null;
    }

    /**
     * 通过key从本地缓存获取单个实体
     *
     * @param key
     * @return
     */
    default T getEntityFromLocalCacheByKey(String key) {
        return null;
    }


    /**
     * 刷新本地缓存
     */
    default void flushLocalCache(){
        System.out.println("刷新本地缓存...");
    }

}
