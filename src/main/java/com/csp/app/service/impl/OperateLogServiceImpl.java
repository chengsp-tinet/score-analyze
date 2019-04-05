package com.csp.app.service.impl;

import com.csp.app.entity.OperateLog;
import com.csp.app.mapper.OperateLogMapper;
import com.csp.app.service.OperateLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author chengsp on 2019年4月5日15:20:27
 */
@Service
public class OperateLogServiceImpl implements OperateLogService {
    @Autowired
    private OperateLogMapper operateLogMapper;

    @Override
    public boolean saveLog(OperateLog operateLog) {
        return operateLogMapper.insert(operateLog) == 1;
    }
}
