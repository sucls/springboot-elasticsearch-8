package com.sucl.springbootelasticsearch8.core.util;

import com.sucl.springbootelasticsearch8.core.query.QueryResult;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;

import java.util.List;

/**
 * @author sucl
 * @date 2023/6/20 10:55
 * @since 1.0.0
 */
public class QueryResultUtils {

    /**
     *
     * @param searchHits
     * @return
     * @param <T>
     */
    public static <T> QueryResult<T> buildResult(SearchHits<T> searchHits){
        List<SearchHit<T>> searchHitList = searchHits.getSearchHits();

        return null;
    }

}
