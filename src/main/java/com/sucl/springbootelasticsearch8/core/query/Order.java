package com.sucl.springbootelasticsearch8.core.query;

import co.elastic.clients.elasticsearch._types.SortMode;
import lombok.Data;

/**
 * 排序参数
 */
@Data
public class Order {
    private String field;
    private boolean asc;
    /**
     * @see SortMode
     */
    private String sortMode;

    public Order(String field, boolean asc, String sortMode) {
        this.field = field;
        this.asc = asc;
        this.sortMode = sortMode;
    }

    public Order(String field, boolean asc) {
        this(field, asc, null);
    }

    public Order(String field, String sortMode) {
        this(field, true, sortMode);
    }

    public Order(String field) {
        this(field, true, null);
    }
}