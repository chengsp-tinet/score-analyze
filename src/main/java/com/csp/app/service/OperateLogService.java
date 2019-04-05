package com.csp.app.service;

import com.csp.app.entity.OperateLog;

/**
 * @author chengsp on 2019年4月5日00:31:56
 */
public interface OperateLogService {
    /**
     * 保存日志
     * @param operateLog
     * @return
     */
    boolean saveLog(OperateLog operateLog);
}
