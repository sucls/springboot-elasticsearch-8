package com.sucl.springbootelasticsearch8.core.query;

import lombok.Data;

import java.util.Map;

/**
 * 聚合查询
 * @author sucl
 * @date 2023/6/13 9:24
 * @since 1.0.0
 */
@Data
public class AggQuery implements Query{

    private Type type;

    private String field;

    private Map<String,Object> options;

    public static AggQuery of(Type type, String field){
        AggQuery aggQuery = new AggQuery();
        aggQuery.type = type;
        aggQuery.field = field;
        return aggQuery;
    }

    /**
     * 聚合类型
     */
    public enum Type{
        Terms,
        Sum,
        Avg,
        Min,
        Max,
        Stats,
        ExtendedStats,
        Cardinality,
        DateHistogram,
        Range,
        Nested
    }
}
