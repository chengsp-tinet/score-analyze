package com.csp.app.common.es;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.searchbox.action.BulkableAction;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.*;
import io.searchbox.core.search.aggregation.MetricAggregation;
import io.searchbox.indices.CloseIndex;
import io.searchbox.indices.DeleteIndex;
import io.searchbox.indices.IndicesExists;
import io.searchbox.indices.OpenIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

/**
 * Created by chengsp 2019年3月22日18:29:07
 */
@Component
@ConfigurationProperties(prefix = "jest")
public class JestService {

    private Logger logger = LoggerFactory.getLogger(JestService.class);

    private String host;

    private int maxTotalConnectionPerRoute;

    private int maxTotalConnection;

    private JestClient client;

    private String userName;

    private String password;

    @Bean
    public JestService JestService_(){
        JestService jestService = new JestService();
        String[] hosts = host.split(";");
        List<String> hostList = Lists.newArrayList();
        for (String s : hosts){
            hostList.add(s);
        }
        JestClientFactory factory = new JestClientFactory();
        HttpClientConfig httpClientConfig = null;
        if(!StringUtils.isEmpty(userName) && !StringUtils.isEmpty(password)){
            httpClientConfig = new HttpClientConfig
                    .Builder(hostList)
                    .multiThreaded(true)
                    .defaultMaxTotalConnectionPerRoute(maxTotalConnectionPerRoute)
                    .maxTotalConnection(maxTotalConnection)
                    .defaultCredentials(userName, password)
                    .build();
        }else{
            httpClientConfig = new HttpClientConfig
                    .Builder(hostList)
                    .multiThreaded(true)
                    .defaultMaxTotalConnectionPerRoute(maxTotalConnectionPerRoute)
                    .maxTotalConnection(maxTotalConnection)
                    .build();
        }
        factory.setHttpClientConfig(httpClientConfig);
        client = factory.getObject();
        return jestService;
    }

    /**
     * 所有单条数据
     * @param o
     * @param indexName
     * @return
     */
    public boolean index(Object o,String indexName){
        return index(o,indexName,"ussdc");
    }

    /**
     * 所有单条数据
     * @param o
     * @param indexName
     * @return
     */
    private boolean index(Object o,String indexName,String indexType){
        try {
            Bulk bulk = new Bulk.Builder()
                    .defaultIndex(indexName)
                    .defaultType(indexType)
                    .addAction(Arrays.asList(toBulkableAction(o))).build();
            JestResult result = client.execute(bulk);
            return result.isSucceeded();
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("index exception:",e);
            return false;
        }
    }

    /*public void update(){
        new Update.Builder()
    }*/

    /**
     * 批量创建索引
     * @param list
     * @param indexName
     * @return
     * @throws IOException
     */
    public boolean bulkIndex(List<? extends Object> list,String indexName) {
        try {
            boolean success = bulkIndex(list, indexName, "ussdc");
            return success;
        }catch (Exception e){
            logger.error("jestService bulkIndex exception:",e);
            return false;
        }
    }

    /**
     * 批量创建索引
     * @param list
     * @param indexName
     * @return
     * @throws IOException
     */
    private boolean bulkIndex(List<? extends Object> list,String indexName,String indexType) {
        try {
            List<BulkableAction> actions = new ArrayList<>();
            list.forEach(o -> actions.add(new Index.Builder(o).build()));
            Bulk bulk = new Bulk.Builder()
                    .defaultIndex(indexName)
                    .defaultType(indexType)
                    .addAction(actions).build();
            JestResult result = client.execute(bulk);
            if(!result.isSucceeded()){
                logger.error(result.getJsonString());
            }
            return result.isSucceeded();
        }catch (IOException e){
            e.printStackTrace();
            logger.error("bulkIndex es exception",e);
        }
        return false;
    }

    /**
     * 查询es
     * @param condition
     * @param <T>
     * @return
     */
    public <T> EsQueryResult<T> search(EsQueryCondition condition){
        EsQueryResult<T> esQueryResult = new EsQueryResult<>();
        try {
            SearchResult result = client.execute(condition.instanceSearch());
            boolean success = result.isSucceeded();
            esQueryResult.setSuccess(success);
            if(!success){
                JsonObject error = result.getJsonObject().getAsJsonObject("error");
                if(error != null){
                    JsonElement reason = error.get("reason");
                    if(reason!=null){
                        logger.error("search fail reason:",reason.getAsString());
                        if("closed".equals(reason.getAsString()) && !condition.isRetry()){
                            condition.getIndexNames().forEach(indexName->openIndex(indexName.toString()));
                            condition.setRetry(true);
                            return search(condition);
                        }
                    }
                }
                return esQueryResult;
            }
            esQueryResult.setTotal(result.getTotal());
            List<SearchResult.Hit<T, Void>> hits = result.getHits(condition.getClasz());
            List<T> ts = Lists.newArrayList();
            if(!hits.isEmpty()){
                hits.forEach( hit ->{
                    if(hit != null){
                        T t = hit.source;
                        ts.add(t);
                    }else {
                        logger.error("hit is null");
                    }
                });
            }
            logger.info("hit count:{}",hits.size());
            esQueryResult.setResult(ts);
            MetricAggregation aggregation = result.getAggregations();
            if(aggregation != null){
                Map<String,Double> aggs = Maps.newHashMap();
                Multimap<String, Object> aggMap = condition.getAggregationsParam();
                if(aggMap != null){
                    aggMap.keys().elementSet().forEach((k) -> {
                        Collection<Object> collection = aggMap.get(k);
                        AggregationsType aggregationsType = (AggregationsType) collection.iterator().next();
                        switch (aggregationsType){
                            case AVG:aggs.put(k,aggregation.getAvgAggregation(k).getAvg());break;
                            case MAX:aggs.put(k,aggregation.getMaxAggregation(k).getMax());break;
                            case MIN:aggs.put(k,aggregation.getMinAggregation(k).getMin());break;
                            case SUM:aggs.put(k,aggregation.getSumAggregation(k).getSum());break;
                        }
                    });
                }

                esQueryResult.setAggregations(aggs);
            }
            return esQueryResult;
        } catch (IOException e) {
            e.printStackTrace();
            esQueryResult.setSuccess(false);
            logger.error("search es exception",e);
        }
        return esQueryResult;
    }

    public SearchResult search(Search search){
        try {
            SearchResult result = client.execute(search);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean update(Object object,String index,String id){
        Update update = new Update.Builder(object).index(index).type(index).id(id).build();
        try {
            JestResult result = client.execute(update);
            return result.isSucceeded();
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("update exception",e);
        }
        return false;
    }

    /**
     * 删除索引
     * @param indexName
     * @return
     */
    public boolean deleteInex(String indexName){
        JestResult jr = null;
        try {
            jr = client.execute(new DeleteIndex.Builder(indexName).build());
            return jr.isSucceeded();
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("deleteInex exception",e);
        }
        return false;
    }

    /**
     * 判断索引是否存在
     * @param indexName
     * @return
     */
    public boolean isExists(String indexName){
        IndicesExists indicesExists = new IndicesExists.Builder(indexName).build();
        try{
           JestResult jestResult = client.execute(indicesExists);
           boolean flag = jestResult.isSucceeded();
           if(flag){
               logger.info("索引存在:"+indexName);
           }
           return flag;
        }catch (IOException e){
            logger.error("isExists exception: ",e);
        }
        return false;
    }

    /**
     *关闭索引
     * @param indexName
     * @return
     */
    public boolean closeIndex(String indexName){
        CloseIndex closeIndex = new CloseIndex.Builder(indexName).build();
        try {
            client.execute(closeIndex);
            logger.info("关闭索引：{}",indexName);
        } catch (IOException e) {
            logger.error("closeIndex exception:",e);
        }
        return false;
    }

    /**
     * 打开索引
     * @param indexName
     * @return
     */
    public boolean openIndex(String indexName){
        OpenIndex openIndex = new OpenIndex.Builder(indexName).build();
        try {
            client.execute(openIndex);
            logger.info("打开索引：{}",indexName);
        } catch (IOException e) {
            logger.error("openIndex exception:",e);
        }
        return false;
    }


    public static BulkableAction toBulkableAction(Object o){
        return new Index.Builder(o).build();
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getMaxTotalConnectionPerRoute() {
        return maxTotalConnectionPerRoute;
    }

    public void setMaxTotalConnectionPerRoute(int maxTotalConnectionPerRoute) {
        this.maxTotalConnectionPerRoute = maxTotalConnectionPerRoute;
    }

    public int getMaxTotalConnection() {
        return maxTotalConnection;
    }

    public void setMaxTotalConnection(int maxTotalConnection) {
        this.maxTotalConnection = maxTotalConnection;
    }

    public JestClient getClient() {
        return client;
    }

    public void setClient(JestClient client) {
        this.client = client;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
