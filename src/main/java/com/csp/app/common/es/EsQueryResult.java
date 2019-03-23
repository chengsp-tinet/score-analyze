package com.csp.app.common.es;

import java.util.List;
import java.util.Map;

/**
 * @author chengsp 2019年3月22日18:28:58
 */
public class EsQueryResult<T> {

    private boolean success;

    private int total;

    private List<T> result;

    private Map<String,Double> aggregations;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<T> getResult() {
        return result;
    }

    public void setResult(List<T> result) {
        this.result = result;
    }

    public Map<String, Double> getAggregations() {
        return aggregations;
    }

    public void setAggregations(Map<String, Double> aggregations) {
        this.aggregations = aggregations;
    }
}
