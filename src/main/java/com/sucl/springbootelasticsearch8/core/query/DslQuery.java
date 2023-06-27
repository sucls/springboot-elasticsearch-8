package com.sucl.springbootelasticsearch8.core.query;

import lombok.Data;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 简单查询
 * @author sucl
 * @date 2023/6/12 15:58
 * @since 1.0.0
 */
@Data
public class DslQuery extends QuerySupport{

    private String field;

    private Object value;

    private Type type;

    public static DslQuery of(Type queryType, String field, Object value){
        DslQuery dslQuery = new DslQuery();
        dslQuery.type = queryType;
        dslQuery.field = field;
        dslQuery.value = value;
        return dslQuery;
    }

    public String getStrValue(){
        return Objects.toString(this.value, "");
    }

    public List<String> getListValue() {
        return Arrays.asList(getStrValue().split(","));
    }

    public List<String> getFields() {
        return this.field != null ? Arrays.asList(field.split(",")): Collections.emptyList();
    }

    /**
     * 简单查询类型
     */
    public enum Type{
        /**
         *
         */
        MATCH_ALL,
        /**
         * 模糊查询（分词）
         */
        MATCH,
        /**
         *
         */
        MULTI_MATCH,
        /**
         * 前缀匹配
         */
        PREFIX,
        /**
         * 正则
         */
        REGEXP,
        /**
         * 通配符
         */
        WILDCARD,
        /**
         * 词条匹配
         */
        TERM,
        /**
         * 多词条匹配
         */
        TERMS,
        /**
         * 范围匹配
         */
        RANGE,
        /**
         * 存在匹配
         */
        EXISTS,
        /**
         * ID
         */
        IDS,
        /**
         * 模糊
         */
        FUZZY,

    }

}
