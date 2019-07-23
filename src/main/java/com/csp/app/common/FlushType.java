package com.csp.app.common;

import com.csp.app.common.flush.DeleteFlush;
import com.csp.app.common.flush.InsertFlush;
import com.csp.app.common.flush.UpdateFlush;
import com.csp.app.util.ContextUtil;

public enum FlushType {
    /**
     * 缓存刷新类型
     */
    INSERT(ContextUtil.getBean(InsertFlush.class)),
    UPDATE(ContextUtil.getBean(UpdateFlush.class)),
    DELETE(ContextUtil.getBean(DeleteFlush.class));
    private CacheFlush cacheFlush;

    FlushType(CacheFlush cacheFlush) {
        this.cacheFlush = cacheFlush;
    }

    public CacheFlush getCacheFlush() {
        return cacheFlush;
    }

    public void setCacheFlush(CacheFlush cacheFlush) {
        this.cacheFlush = cacheFlush;
    }

    public void flush(String key, Object data) {
        cacheFlush.doFlush(key, data);
    }
}
