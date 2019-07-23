package com.csp.app.common;

import com.alibaba.druid.filter.FilterEventAdapter;
import com.alibaba.druid.proxy.jdbc.StatementProxy;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.csp.app.service.CacheService;
import com.csp.app.service.RedisService;
import com.csp.app.util.ContextUtil;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Commit;
import net.sf.jsqlparser.statement.SetStatement;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.StatementVisitor;
import net.sf.jsqlparser.statement.Statements;
import net.sf.jsqlparser.statement.alter.Alter;
import net.sf.jsqlparser.statement.create.index.CreateIndex;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.create.view.AlterView;
import net.sf.jsqlparser.statement.create.view.CreateView;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.execute.Execute;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.merge.Merge;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.statement.upsert.Upsert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.util.StringUtil;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author chengsp on 2018/12/4.
 */
@Component
public class SqlFilter extends FilterEventAdapter {

    private static Logger logger = LoggerFactory.getLogger(SqlFilter.class);
    private final static Pattern PATTERN = Pattern.compile("\\binsert\\b|\\bupdate\\b|\\bdelete\\b");
    private static CCJSqlParserManager parserManager = new CCJSqlParserManager();

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
        if (result) {
            return;
        }
        sql = sql.toLowerCase();
        if (sql.contains("select")) {
            return;
        }
        if (!PATTERN.matcher(sql).find()) {
            return;
        }
        List<String> tables = getTables(sql);
        if (CollectionUtils.isEmpty(tables)) {
            return;
        }
        String  tableName = tables.get(0);
        String beanName = StringUtil.underlineToCamelhump(tableName) + "ServiceImpl";
        CacheService cacheService = (CacheService) ContextUtil.getBean(beanName);
        cacheService.loadCache();
        redisService.publish(Const.DEFAULT_CHANNEL, beanName);
    }

    @Override
    protected void statementExecuteBatchAfter(StatementProxy statement, int[] result) {
        String batchSql = statement.getBatchSql();
        statementExecuteAfter(statement, batchSql, false);
    }

    static class MyStatementVisitor implements StatementVisitor {
        List<String> tableNames;

        public MyStatementVisitor(List<String> tableNames) {
            this.tableNames = tableNames;
        }

        @Override
        public void visit(Commit commit) {

        }

        //访问delete语句
        @Override
        public void visit(Delete delete) {
            tableNames.add(delete.getTable().getName());
        }

        //访问update语句
        @Override
        public void visit(Update update) {
            List<Table> tables = update.getTables();
            for (Table table : tables) {
                tableNames.add(table.getName());
            }
        }

        //访问insert语句
        @Override
        public void visit(Insert insert) {
            tableNames.add(insert.getTable().getName());
        }

        @Override
        public void visit(Replace replace) {
        }

        @Override
        public void visit(Drop drop) {
        }

        @Override
        public void visit(Truncate truncate) {
        }

        @Override
        public void visit(CreateIndex createIndex) {

        }

        @Override
        public void visit(CreateTable createTable) {

        }

        @Override
        public void visit(CreateView createView) {

        }

        @Override
        public void visit(AlterView alterView) {

        }

        @Override
        public void visit(Alter alter) {

        }

        @Override
        public void visit(Statements statements) {

        }

        @Override
        public void visit(Execute execute) {

        }

        @Override
        public void visit(SetStatement setStatement) {

        }

        @Override
        public void visit(Merge merge) {

        }

        @Override
        public void visit(Select select) {
        }

        @Override
        public void visit(Upsert upsert) {

        }
    }

    /**
     * 从sql中提取表名
     *
     * @param sql
     * @return
     */
    private static List<String> getTables(String sql) {
        Statement stmt;
        try {
            //解析SQL语句
            stmt = parserManager.parse(new StringReader(sql));
        } catch (JSQLParserException e) {
            return null;
        }
        final List<String> tableNames = new ArrayList<>();
        //使用visitor模式访问SQL的各个组成部分
        stmt.accept(new MyStatementVisitor(tableNames));
        return tableNames;
    }
}
