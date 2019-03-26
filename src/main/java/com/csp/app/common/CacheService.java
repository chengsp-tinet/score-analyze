package com.csp.app.common;

/**
 * 缓存服务
 * @author chengsp 2019年3月23日11:39:57
 */
public interface CacheService<T> {
    /**
     * 缓存单条记录
     * @param t
     */
    default void loadCache(T t) {
    }

    /**
     * 缓存所有记录
     */
    default void loadCache() {
    }

    /**
     * 通过key从缓存获取单个实体
     *
     * @param args
     * @return
     */
    default T getEntityFromCacheByKey(Object... args) {
        return null;
    }

    /**
     * 通过key从本地缓存获取单个实体
     *
     * @param args
     * @return
     */
    default T getEntityFromLocalCacheByKey(Object... args) {
        return null;
    }

    /**
     * 计算key
     * @param args
     * @return
     */
    default String getKey(String... args) {
        return null;
    }

    /**
     * 更新本地缓存
     * @param key
     */
    default void loadLocalCacheFromRedis(String key){}
}
