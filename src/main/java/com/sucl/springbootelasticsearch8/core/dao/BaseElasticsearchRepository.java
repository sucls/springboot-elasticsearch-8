package com.sucl.springbootelasticsearch8.core.dao;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.sucl.springbootelasticsearch8.core.query.*;
import com.sucl.springbootelasticsearch8.core.util.DslQueryBuilderUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.repository.support.ElasticsearchEntityInformation;
import org.springframework.data.elasticsearch.repository.support.SimpleElasticsearchRepository;

import java.util.List;

/**
 * @author sucl
 * @date 2023/6/6 19:05
 * @since 1.0.0
 */
public class BaseElasticsearchRepository<T,ID> extends SimpleElasticsearchRepository<T,ID> implements ElasticsearchDao<T,ID>{

    private ElasticsearchEntityInformation entityInformation;
    private ElasticsearchOperations elasticsearchOperations;

    public BaseElasticsearchRepository(ElasticsearchEntityInformation metadata, ElasticsearchOperations operations) {
        super(metadata, operations);
        this.entityInformation = metadata;
        this.elasticsearchOperations = operations;
    }

    /**
     *
     * @param dslQuery
     * @param orders
     * @return
     */
    @Override
    public List<T> commonQuery(DslQuery dslQuery, List<Order> orders){
        NativeQueryBuilder queryBuilder = new NativeQueryBuilder()
                .withQuery(DslQueryBuilderUtils.buildQueryBuilder(dslQuery).build());

        List<SortOptions> sortOptions = DslQueryBuilderUtils.buildSorts(orders);
        if(!sortOptions.isEmpty()){
            queryBuilder.withSort(sortOptions);
        }

        SearchHits searchHits = elasticsearchOperations.search(queryBuilder.build(), entityInformation.getJavaType(), entityInformation.getIndexCoordinates());
        return searchHits.getSearchHits();
    }

    /**
     *
     * @param dslQuery
     * @param orders
     * @param pager
     * @return
     */
    @Override
    public Page<T> commonPageQuery(DslQuery dslQuery, List<Order> orders, Pager pager){
        Pageable pageRequest = PageRequest.of(pager.getPageIndex(), pager.getPageSize());
        NativeQueryBuilder queryBuilder = new NativeQueryBuilder()
                .withQuery(DslQueryBuilderUtils.buildQueryBuilder(dslQuery).build())
                .withPageable(pageRequest);

        List<SortOptions> sortOptions = DslQueryBuilderUtils.buildSorts(orders);
        if(!sortOptions.isEmpty()){
            queryBuilder.withSort(sortOptions);
        }

        SearchHits searchHits = elasticsearchOperations.search(queryBuilder.build(), entityInformation.getJavaType(), entityInformation.getIndexCoordinates());
        SearchPage searchPage = SearchHitSupport.searchPageFor(searchHits, pageRequest);
        return (Page<T>) SearchHitSupport.unwrapSearchHits(searchPage);
    }

    /**
     *
     * @param compoundQuery
     * @param orders
     * @return
     */
    @Override
    public List<T> compoundQuery(CompoundQuery compoundQuery, List<Order> orders){
        NativeQueryBuilder queryBuilder = new NativeQueryBuilder()
                .withQuery(DslQueryBuilderUtils.buildQueryBuilder(compoundQuery).build());

        List<SortOptions> sortOptions = DslQueryBuilderUtils.buildSorts(orders);
        if(!sortOptions.isEmpty()){
            queryBuilder.withSort(sortOptions);
        }
        SearchHits searchHits = elasticsearchOperations.search(queryBuilder.build(), entityInformation.getJavaType(), entityInformation.getIndexCoordinates());
        return searchHits.getSearchHits();
    }

    /**
     *
     * @param compoundQuery
     * @param orders
     * @param pager
     * @return
     */
    @Override
    public Page<T> compoundPageQuery(CompoundQuery compoundQuery, List<Order> orders, Pager pager){
        Pageable pageRequest = PageRequest.of(pager.getPageIndex(), pager.getPageSize());
        NativeQueryBuilder queryBuilder = new NativeQueryBuilder()
                .withQuery(DslQueryBuilderUtils.buildQueryBuilder(compoundQuery).build())
                .withPageable(pageRequest);

        List<SortOptions> sortOptions = DslQueryBuilderUtils.buildSorts(orders);
        if(!sortOptions.isEmpty()){
            queryBuilder.withSort(sortOptions);
        }
        SearchHits searchHits = elasticsearchOperations.search(queryBuilder.build(), entityInformation.getJavaType(), entityInformation.getIndexCoordinates());
        SearchPage searchPage = SearchHitSupport.searchPageFor(searchHits, pageRequest);
        return (Page<T>) SearchHitSupport.unwrapSearchHits(searchPage);
    }

    /**
     *
     * @param compoundQuery
     * @param aggQueries
     * @return
     */
    @Override
    public SearchHits<T> aggregation(CompoundQuery compoundQuery, List<AggQuery> aggQueries) {
        NativeQueryBuilder queryBuilder = new NativeQueryBuilder()
                .withQuery(DslQueryBuilderUtils.buildQueryBuilder(compoundQuery).build());
        // agg
        DslQueryBuilderUtils.buildAggregations(aggQueries).forEach((name,agg)->{
            queryBuilder.withAggregation(name, agg);
        });
        return elasticsearchOperations.search(queryBuilder.build(), entityInformation.getJavaType(), entityInformation.getIndexCoordinates());
    }

    /**
     *
     * @param compoundQuery
     * @param aggQueries
     * @param pager
     * @return
     */
    @Override
    public Page<T> aggregationPage(CompoundQuery compoundQuery, List<AggQuery> aggQueries, Pager pager) {
        Pageable pageRequest = PageRequest.of(pager.getPageIndex(), pager.getPageSize());
        NativeQueryBuilder queryBuilder = new NativeQueryBuilder()
                .withQuery(DslQueryBuilderUtils.buildQueryBuilder(compoundQuery).build());
        // agg
        DslQueryBuilderUtils.buildAggregations(aggQueries).forEach((name,agg)->{
            queryBuilder.withAggregation(name, agg);
        });
        // page
        queryBuilder.withPageable(pageRequest);
        SearchHits searchHits = elasticsearchOperations.search(queryBuilder.build(), entityInformation.getJavaType(), entityInformation.getIndexCoordinates());
        SearchPage searchPage = SearchHitSupport.searchPageFor(searchHits, pageRequest);
        return (Page<T>) SearchHitSupport.unwrapSearchHits(searchPage);
    }

}
