package com.csp.app.common;

import com.alibaba.druid.filter.FilterEventAdapter;
import com.alibaba.druid.proxy.jdbc.StatementProxy;
import com.csp.app.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author chengsp on 2018/12/4.
 */
@Component
public class SqlFilter extends FilterEventAdapter {

    private Logger logger = LoggerFactory.getLogger(SqlFilter.class);

    @Autowired
    private List<CacheService> cacheServiceList;

    @Autowired
    private RedisUtil redisService;

    /**
     * 根据sql执行结果判定是否更新缓存，做pub广播
     * @param statement
     * @param sql
     * @param result
     */
    @Override
    protected void statementExecuteAfter(StatementProxy statement, String sql, boolean result) {
        //查询返回true，更新或插入返回false
        logger.info("执行sql:{}",sql);
        if(result == false){
            sql = sql.toLowerCase();
            if((sql.contains("insert") || sql.contains("update") ||  sql.contains("delete")) && !sql.contains("select")){
                for (CacheService cacheService : cacheServiceList){
                   /* //去掉clid表自动load缓存
                    if("clid".equals(cacheService.getTableName())){
                        continue;
                    }
                    if(sql.contains(cacheService.getTableName())){
                        logger.info("sql:{}",sql);
                        logger.info("table:{}",cacheService.getTableName());
                        Set<String> keys = cacheService.reloadCache();
                        if(CollectionUtils.isNotEmpty(keys)){
                            redisService.convertAndSend(CacheKey.REDIS_PUB_SUB_DB_CACHE, Strings.join(keys ,','));
                        }
                    }*/
                }
            }
        }
    }

    @Override
    protected void statementExecuteBatchAfter(StatementProxy statement, int[] result) {
        String batchSql = statement.getBatchSql();
        //logger.info("批量操作sql:"+batchSql);
        super.statementExecuteBatchAfter(statement, result);
    }
}
