package com.sucl.springbootelasticsearch8.core.query;

import lombok.Data;

/**
 * 通用条件
 * @author sucl
 * @date 2023/6/12 15:24
 * @since 1.0.0
 */
@Data
public class Condition {
    /**
     * 字段名
     */
    private String field;
    /**
     * 比较类型
     */
    private String operation;
    /**
     * 值
     */
    private Object value;
    /**
     * 查询类型
     */
    private String queryType;
}
