package com.sucl.springbootelasticsearch8.core.dao;

import com.sucl.springbootelasticsearch8.core.query.*;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * @author sucl
 * @date 2023/6/6 19:06
 * @since 1.0.0
 */
public interface ElasticsearchDao<T, ID> extends ElasticsearchRepository<T, ID>{

    List<T> commonQuery(DslQuery dslQuery, List<Order> orders);

    Page<T> commonPageQuery(DslQuery dslQuery, List<Order> orders, Pager pager);

    List<T> compoundQuery(CompoundQuery compoundQuery, List<Order> orders);

    Page<T> compoundPageQuery(CompoundQuery compoundQuery, List<Order> orders, Pager pager);

    SearchHits<T> aggregation(CompoundQuery compoundQuery, List<AggQuery> aggQueries);

    Page<T> aggregationPage(CompoundQuery compoundQuery, List<AggQuery> aggQueries, Pager pager);
}
