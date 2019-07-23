package com.csp.app.service;

public interface CacheService<T> {
    /**
     * 缓存所有记录
     */
    default void loadCache() {
    }


    /**
     * 通过key从缓存获取单个实体
     *
     * @param keyPattern
     * @param args
     * @return
     */
    default T getEntityFromCacheByKey(String keyPattern, Object... args) {
        String key = String.format(keyPattern, args);
        return getEntityFromCacheByKey(key);
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
     * 刷新本地缓存
     */
    default void flushLocalCache(String key) {
        System.out.println("刷新本地缓存...");
    }

}
