package com.csp.app.common;

import com.alibaba.druid.filter.FilterEventAdapter;
import com.alibaba.druid.proxy.jdbc.StatementProxy;
import com.csp.app.service.RedisService;
import com.csp.app.util.CommUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

/**
 * @author chengsp on 2018/12/4.
 */
@Component
public class SqlFilter extends FilterEventAdapter {

    private static Logger logger = LoggerFactory.getLogger(SqlFilter.class);

    @Autowired
    private List<CacheService> cacheServiceList;

    @Autowired
    private RedisService redisService;

    @Override
    protected void statementExecuteBefore(StatementProxy statement, String sql) {
        super.statementExecuteBefore(statement, sql);
    }

    /**
     * 根据sql执行结果判定是否更新缓存，做pub广播
     *
     * @param statement
     * @param sql
     * @param result
     */
    @Override
    protected void statementExecuteAfter(StatementProxy statement, String sql, boolean result) {
        //查询返回true，更新或插入返回false
        if (!result) {
            sql = sql.toLowerCase();
            if (!sql.contains("select")) {
                if ((sql.contains("insert") || sql.contains("update") || sql.contains("delete"))) {
                    for (CacheService cacheService : cacheServiceList) {
                        String classFullName = cacheService.getClass().getName();
                        String className = classFullName.substring(classFullName.lastIndexOf(".") + 1);
                        String entityName = className.substring(0, className.lastIndexOf("ServiceImpl"));
                        String tableName = CommUtil.humpToLine(entityName);
                        String regex = "[^\\w]" + tableName + "[^\\w]";
                        if (Pattern.compile(regex).matcher(sql).find()) {
                            cacheService.loadCache();
                            String beanName = entityName.substring(0, 1).toLowerCase() + entityName.substring(1) + "ServiceImpl";
                            redisService.publish(Const.DEFAULT_CHANNEL, beanName);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void statementExecuteBatchAfter(StatementProxy statement, int[] result) {
        String batchSql = statement.getBatchSql();
        statementExecuteAfter(statement,batchSql, false);
    }
}
