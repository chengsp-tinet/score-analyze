package com.csp.app.common.es;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import io.searchbox.core.Search;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by chengsp on 2019年3月22日18:28:38
 */
public class EsQueryCondition<T> {

    private Logger logger = LoggerFactory.getLogger(EsQueryCondition.class);

    /**
     * 需要查询的索引
     */
    private Set<String> indexNames = Sets.newHashSet();

    /**
     * 精确匹配查询参数
     */
    private Map<String, Object> param;

    /**
     * 模糊匹配
     */
    private Map<String, Object> matchQueryParam;

    /**
     * 范围查询参数
     */
    private Multimap<String, Object> rangeQueryParam;

    /**
     * 统计数据,例如：durationSum
     */
    private Multimap<String, Object> aggregationsParam;

    /**
     * 排序
     */
    private Map<String, SortOrder> sortParam;

    /**
     * not null 匹配
     */
    private Set<String> exits;

    /**
     * 每页返回数据条数
     */
    private int limit;

    /**
     * 查询起始位
     */
    private int offset;

    /**
     * 数据对象类型
     */
    private Class<T> clasz;

    /**
     * 重试查询，有索引关闭时，会打开索引重新查询
     */
    private boolean retry;

    /**
     * 聚合查询的大小
     */
    private int termsSize;

    /**
     * 用于一个字段对应多个值的参数
     */
    private Map<String, List<T>> queryParam;

    /**
     * 生产查询条件
     *
     * @return
     */
    public Search instanceSearch() {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        if (param != null) {
            param.forEach((k, v) -> boolQueryBuilder.filter(QueryBuilders.termsQuery(k, v)));
        }

        if (queryParam != null) {
            queryParam.forEach((k, v) -> boolQueryBuilder.filter(QueryBuilders.termsQuery(k, v)));
        }

        if (limit > 0) {
            searchSourceBuilder.size(limit);
        }
        if (offset > 0) {
            searchSourceBuilder.from(offset);
        }
        if (rangeQueryParam != null) {
            rangeQueryParam.keys().elementSet().forEach((k) -> {
                Collection<Object> collection = rangeQueryParam.get(k);
                Iterator<Object> iterator = collection.iterator();
                Object start = null;
                Object end = null;
                if (iterator.hasNext()) {
                    start = iterator.next();
                }
                if (iterator.hasNext()) {
                    end = iterator.next();
                }
                RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(k);
                if (start != null) {
                    rangeQueryBuilder.gte(start);
                }
                if (end != null) {
                    rangeQueryBuilder.lte(end);
                }
                if (start != null || end != null) {
                    boolQueryBuilder.filter(rangeQueryBuilder);
                }
            });
        }

        if (exits != null) {
            exits.forEach(name -> boolQueryBuilder.filter(QueryBuilders.existsQuery(name)));
        }
        if (matchQueryParam != null) {
            matchQueryParam.forEach((k, v) -> boolQueryBuilder.filter(QueryBuilders.matchPhraseQuery(k, v)));
        }
        searchSourceBuilder.query(boolQueryBuilder);

        if (sortParam != null) {
            sortParam.forEach((k, v) -> searchSourceBuilder.sort(k, v));
        }
        if (aggregationsParam != null) {
            aggregationsParam.keys().elementSet().forEach(k -> {
                Collection<Object> collection = aggregationsParam.get(k);
                Iterator<Object> iterator = collection.iterator();
                AggregationsType type = (AggregationsType) iterator.next();
                String fieldName = (String) iterator.next();
                switch (type) {
                    case SUM: {
                        searchSourceBuilder.aggregation(AggregationBuilders.sum(k).field(fieldName));
                        break;
                    }
                    case MIN: {
                        searchSourceBuilder.aggregation(AggregationBuilders.min(k).field(fieldName));
                        break;
                    }
                    case MAX: {
                        searchSourceBuilder.aggregation(AggregationBuilders.max(k).field(fieldName));
                        break;
                    }
                    case AVG: {
                        searchSourceBuilder.aggregation(AggregationBuilders.avg(k).field(fieldName));
                        break;
                    }
                    case TERMS: {
                        if (termsSize > 0) {
                            searchSourceBuilder.aggregation(AggregationBuilders.terms(k).field(fieldName).size(termsSize));
                        } else {
                            searchSourceBuilder.aggregation(AggregationBuilders.terms(k).field(fieldName));
                        }

                        break;
                    }
                    default:
                        break;
                }
            });
        }

        String json = searchSourceBuilder.toString();
//        logger.info(json);
        Search.Builder builder = new Search.Builder(json);
        indexNames.forEach(indexName -> builder.addIndex(indexName));

        return builder.build();
    }

    /**
     * 创建查询
     *
     * @param indexName
     * @return
     */
    public static EsQueryCondition newEsQueryCondition(String... indexName) {
        EsQueryCondition esQueryCondition = new EsQueryCondition();
        for (String s : indexName) {
            esQueryCondition.addIndex(s);
        }
        return esQueryCondition;
    }

    /**
     * 增加查询条件
     *
     * @param paramName
     * @param paramValue
     * @return
     */
    public EsQueryCondition addParam(String paramName, Object paramValue) {
        if (this.param == null) {
            this.param = new HashMap<>();
        }
        this.param.put(paramName, paramValue);
        return this;
    }

    /**
     * 增加一个字段多值的条件
     *
     * @param paramName
     * @param paramValue
     * @return
     */
    public EsQueryCondition addQueryParam(String paramName, List<T> paramValue) {
        if (this.queryParam == null) {
            this.queryParam = new HashMap<>();
        }
        this.queryParam.put(paramName, paramValue);
        return this;
    }

    /**
     * 用对象中的属性值作为查询条件
     *
     * @param object
     * @param clasz
     * @param notParamFieldName 不需要作为查询条件的属性名
     * @param <K>
     * @return
     */
    public <K> EsQueryCondition addParamByBean(Object object, Class<K> clasz, String... notParamFieldName) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(clasz, Introspector.IGNORE_ALL_BEANINFO);
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                String name = propertyDescriptor.getName();
                boolean addParam = true;
                for (String string : notParamFieldName) {
                    if (string.equals(name)) {
                        addParam = false;
                        break;
                    }
                }
                if (addParam && !"class".equals(name)) {
                    Method method = propertyDescriptor.getReadMethod();
                    if (method == null) {
                        logger.error("no ReadMethor {}", name);
                        continue;
                    }
                    Object value = method.invoke(object);
                    if (value != null) {
                        if (value instanceof String) {
                            String str = (String) value;
                            if (StringUtils.isEmpty(str)) {
                                continue;
                            }
                            name = name + ".keyword";
                        }
                        this.addParam(name, value);
                    }
                }
            }
        } catch (IntrospectionException e) {
            logger.error("addParamByBean IntrospectionException:", e);
        } catch (IllegalAccessException e) {
            logger.error("addParamByBean IllegalAccessException:", e);
        } catch (InvocationTargetException e) {
            logger.error("addParamByBean InvocationTargetException:", e);
        } catch (Exception e) {
            logger.error("addParamByBean Exception:", e);
        }
        return this;
    }

    /**
     * 增加模糊匹配查询参数
     *
     * @param paramName
     * @param paramValue
     * @return
     */
    public EsQueryCondition addMatchParam(String paramName, Object paramValue) {
        if (this.matchQueryParam == null) {
            this.matchQueryParam = new HashMap<>();
        }
        this.matchQueryParam.put(paramName, paramValue);
        return this;
    }

    /**
     * 增加查询索引
     *
     * @param indexName
     * @return
     */
    public EsQueryCondition addIndex(String indexName) {
        this.indexNames.add(indexName);
        return this;
    }

    /**
     * 增加排序参数
     *
     * @param paramName
     * @param sortType
     * @return
     */
    public EsQueryCondition addSortParam(String paramName, SortOrder sortType) {
        if (this.sortParam == null) {
            this.sortParam = new HashMap<>();
        }
        this.sortParam.put(paramName, sortType);
        return this;
    }

    /**
     * 增加范围查询条件
     *
     * @param paramName
     * @param start
     * @param end
     * @return
     */
    public EsQueryCondition addRangeQueryParam(String paramName, Object start, Object end) {
        if (this.rangeQueryParam == null) {
            this.rangeQueryParam = ArrayListMultimap.create();
        }
        this.rangeQueryParam.put(paramName, start);
        this.rangeQueryParam.put(paramName, end);
        return this;
    }

    /**
     * 增加统计项
     *
     * @param aggName
     * @param type
     * @param fieldName
     * @return
     */
    public EsQueryCondition addAggregationsParam(String aggName, AggregationsType type, String fieldName) {
        if (this.aggregationsParam == null) {
            this.aggregationsParam = ArrayListMultimap.create();
        }
        this.aggregationsParam.put(aggName, type);
        this.aggregationsParam.put(aggName, fieldName);
        return this;
    }

    public Map<String, Object> getParam() {
        return param;
    }

    public void setParam(Map<String, Object> param) {
        this.param = param;
    }

    public int getLimit() {
        return limit;
    }

    public EsQueryCondition setLimit(int limit) {
        this.limit = limit;
        return this;
    }

    public int getOffset() {
        return offset;
    }

    public EsQueryCondition setOffset(int offset) {
        this.offset = offset;
        return this;
    }

    public Class<T> getClasz() {
        return clasz;
    }

    public EsQueryCondition setClasz(Class<T> clasz) {
        this.clasz = clasz;
        return this;
    }

    public Multimap<String, Object> getAggregationsParam() {
        return aggregationsParam;
    }

    public Set<String> getIndexNames() {
        return indexNames;
    }

    public boolean isRetry() {
        return retry;
    }

    public void setRetry(boolean retry) {
        this.retry = retry;
    }

    public int getTermsSize() {
        return termsSize;
    }

    public void setTermsSize(int termsSize) {
        this.termsSize = termsSize;
    }

    public Map<String, List<T>> getQueryParam() {
        return queryParam;
    }

    public void setQueryParam(Map<String, List<T>> queryParam) {
        this.queryParam = queryParam;
    }

}
