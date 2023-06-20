package com.sucl.springbootelasticsearch8.core.query;

import lombok.Data;

/**
 * 分页条件
 */
@Data
public class Pager {
    private int pageIndex;
    private int pageSize;

    public static Pager of(int pageIndex, int pageSize) {
        Pager pager = new Pager();
        pager.pageIndex = pageIndex;
        pager.pageSize = pageSize;
        return pager;
    }
}