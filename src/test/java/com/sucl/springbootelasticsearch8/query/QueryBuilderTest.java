package com.sucl.springbootelasticsearch8.query;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.TermsQueryField;
import com.sucl.springbootelasticsearch8.core.query.DslQuery;
import com.sucl.springbootelasticsearch8.core.query.Order;
import com.sucl.springbootelasticsearch8.core.util.DslQueryBuilderUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;

import java.util.Arrays;

/**
 * @author sucl
 * @date 2023/6/16 9:08
 * @since 1.0.0
 */
@Slf4j
public class QueryBuilderTest {

    /**
     *
     */
    @Test
    public void printQueryJson(){
        Pageable pageRequest = PageRequest.of(1, 30);
        DslQuery dslQuery = new DslQuery();
        dslQuery.setType(DslQuery.Type.MATCH);
        dslQuery.setField("title");
        dslQuery.setValue("天下");

        Query query = DslQueryBuilderUtils.buildQueryBuilder(dslQuery).build();
        NativeQueryBuilder queryBuilder = new NativeQueryBuilder()
                .withQuery(query)
                .withPageable(pageRequest);

        log.info("query:{}", queryBuilder.build().getQuery());
        log.info("page:{}", queryBuilder.build());
    }

    /**
     *
     */
    @Test
    public void printSortJson() {
        log.info("sort:{}", DslQueryBuilderUtils.buildSorts(Arrays.asList(new Order("name","Max"))) );
    }

    /**
     *
     */
    @Test
    public void printTermsJson() {
        Query.Builder builder = new Query.Builder();
        TermsQueryField queryField = new TermsQueryField.Builder().value(Arrays.asList(FieldValue.of("1"),FieldValue.of("2"))).build();
//        TermsQueryField queryField = new TermsQueryField.Builder().lookup(TermsLookup.of(lk->lk.id("x").index("index_novel").path("content"))).build();
        Query query = builder.terms(QueryBuilders.terms().terms(queryField).field("abc").build()).build();
        log.info("{terms:}", query );
    }

}
