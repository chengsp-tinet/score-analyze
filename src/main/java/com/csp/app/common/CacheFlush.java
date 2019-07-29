package com.csp.app.common;

import com.csp.app.entity.SynMessage;

public interface CacheFlush {
    void doFlush(SynMessage synMessage);
}
