package com.sucl.springbootelasticsearch8.core.util;

import co.elastic.clients.elasticsearch._types.*;
import co.elastic.clients.elasticsearch._types.aggregations.*;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import com.sucl.springbootelasticsearch8.core.query.AggQuery;
import com.sucl.springbootelasticsearch8.core.query.CompoundQuery;
import com.sucl.springbootelasticsearch8.core.query.DslQuery;
import com.sucl.springbootelasticsearch8.core.query.Order;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author sucl
 * @date 2023/6/13 9:15
 * @since 1.0.0
 */
@Slf4j
public class DslQueryBuilderUtils {

    public static Query.Builder newQueryBuilder(){
        return new Query.Builder(){
            @Override
            public Query build() {
                Query query = super.build();
                log.info("query:{}", query);
                return query;
            }
        };
    }

    /**
     * 查询
     * @param dslQuery
     * @param orders
     * @return
     */
    public static Query.Builder buildQueryBuilder(DslQuery dslQuery){
        Query.Builder builder = newQueryBuilder();
        switch (dslQuery.getType()){
            case MATCH_ALL -> {
                builder.matchAll(QueryBuilders.matchAll().build());
                break;
            }
            case MATCH -> {
                builder.match(QueryBuilders.match().query(dslQuery.getStrValue()).field(dslQuery.getField()).build());
                break;
            }
            case MULTI_MATCH -> {
                builder.multiMatch(QueryBuilders.multiMatch().fields(dslQuery.getFields()).query(dslQuery.getStrValue()).build());
                break;
            }
            case PREFIX -> {
                builder.prefix(QueryBuilders.prefix().field(dslQuery.getField()).value(dslQuery.getStrValue()).build());
                break;
            }
            case REGEXP -> {
                builder.regexp(QueryBuilders.regexp().field(dslQuery.getField()).value(dslQuery.getStrValue()).build());
                break;
            }
            case WILDCARD -> {
                builder.wildcard(QueryBuilders.wildcard().field(dslQuery.getField()).value(dslQuery.getStrValue()).build());
                break;
            }
            case TERM -> {
                builder.term(QueryBuilders.term().field(dslQuery.getField()).value(dslQuery.getStrValue()).build());
                break;
            }
            case TERMS -> {
                builder.terms(QueryBuilders.terms()
                        .terms(TermsQueryField.of(er-> er.value(dslQuery.getListValue().stream().map(FieldValue::of).collect(Collectors.toList()))))
                        .field(dslQuery.getField()).build());
                break;
            }
            case RANGE -> {
                // fixme
                builder.range(QueryBuilders.range()
                        .field(dslQuery.getField())
                        .from(dslQuery.getListValue().get(0))
                        .to(dslQuery.getListValue().get(1)).build());
                break;
            }
            case EXISTS -> {
                builder.exists(QueryBuilders.exists().field(dslQuery.getField()).build());
                break;
            }
            case IDS -> {
                builder.ids(QueryBuilders.ids().values(dslQuery.getListValue()).build()).build();
                break;
            }
            case FUZZY -> {
                builder.fuzzy(QueryBuilders.fuzzy().field(dslQuery.getField()).value(dslQuery.getStrValue()).build()).build();
                break;
            }
            default -> {
                //
            }
        }
        return builder;
    }

    /**
     *
     * @param compoundQuery
     * @return
     */
    public static Query.Builder buildQueryBuilder(CompoundQuery compoundQuery){
        Query.Builder queryBuilder = newQueryBuilder();
        if( compoundQuery.getDslQueries() == null ){
            return queryBuilder;
        }
        if( compoundQuery.getType() == null ){
            return buildQueryBuilder(compoundQuery.getDslQueries().get(0));
        }
        switch ( compoundQuery.getType() ){
            case BOOL_QUERY -> {
                queryBuilder.bool(er->{
                    return er;
                });
            }
            case DIS_MAX -> {
                queryBuilder.disMax(builder->{
                    List<Query> queries = compoundQuery.getDslQueries().stream().map(DslQueryBuilderUtils::buildQueryBuilder).map(b->b.build()).collect(Collectors.toList());
                    return builder.queries(queries).tieBreaker(compoundQuery.getDoubleValue());
                });
            }
            case CONSTANT_SCORE -> {
                queryBuilder.constantScore(builder->{
                    Optional<Query> optionalQuery = compoundQuery.getDslQueries().stream().findFirst().map(DslQueryBuilderUtils::buildQueryBuilder).map(b -> b.build());
                    return builder.filter(optionalQuery.orElseGet(()->null)).boost(compoundQuery.getFloatValue());
                });
            }
            case FUNCTION_SCORE -> {
                queryBuilder.functionScore(builder->{
                    Optional<Query> optionalQuery = compoundQuery.getDslQueries().stream().findFirst().map(DslQueryBuilderUtils::buildQueryBuilder).map(b -> b.build());
                    return builder.query(optionalQuery.orElseGet(()->null))
                            .boost(compoundQuery.getFloatValue())
                            .boostMode(FunctionBoostMode.Multiply)
                            .scoreMode(FunctionScoreMode.Multiply)
//                            .functions(Arrays.asList(FunctionScore.of(fn->fn.)))
                            ;
                });
            }
        }
        return queryBuilder;
    }

    /**
     * 排序
     * @param orders
     * @return
     */
    public static List<SortOptions> buildSorts(List<Order> orders){
        List<SortOptions> sortOptions = new ArrayList<>();
        if( orders != null && orders.size() >0 ){
            for (Order order : orders) {
                FieldSort.Builder fieldSortBuilder = new FieldSort.Builder().field(order.getField());
                if( order.getSortMode() != null ){
                    fieldSortBuilder.mode(SortMode.valueOf(order.getSortMode()));
                }else{
                    fieldSortBuilder.order(order.isAsc() ? SortOrder.Asc : SortOrder.Desc);
                }
                sortOptions.add( new SortOptions.Builder().field(fieldSortBuilder.build()).build() );
            }
        }
        return sortOptions;
    }

    /**
     *
     * @param aggQueries
     * @return
     */
    public static Map<String,Aggregation> buildAggregations(List<AggQuery> aggQueries) {
        Map<String,Aggregation> aggregationMap = new LinkedHashMap<>();
        for (AggQuery aggQuery : aggQueries) {
            Aggregation.Builder aggBuilder = new Aggregation.Builder();
            Aggregation.Builder.ContainerBuilder containerBuilder = null;
            switch (aggQuery.getType()){
                case Terms -> {
                    containerBuilder = aggBuilder.terms(TermsAggregation.of(builder -> builder.field(aggQuery.getField())));
                    break;
                }
                case Sum -> {
                    containerBuilder = aggBuilder.sum(SumAggregation.of(builder -> builder.field(aggQuery.getField())));
                    break;
                }
                case Avg -> {
                    containerBuilder = aggBuilder.avg(AverageAggregation.of(builder -> builder.field(aggQuery.getField())));
                    break;
                }
                case Min -> {
                    containerBuilder = aggBuilder.min(MinAggregation.of(builder -> builder.field(aggQuery.getField())));
                    break;
                }
                case Max -> {
                    containerBuilder = aggBuilder.max(MaxAggregation.of(builder -> builder.field(aggQuery.getField())));
                    break;
                }
                case Stats -> {
                    containerBuilder = aggBuilder.stats(StatsAggregation.of(builder -> builder.field(aggQuery.getField())));
                    break;
                }
                case ExtendedStats -> {
                    containerBuilder = aggBuilder.extendedStats(ExtendedStatsAggregation.of(builder -> builder.field(aggQuery.getField())));
                    break;
                }
                case Cardinality -> {
                    containerBuilder = aggBuilder.cardinality(CardinalityAggregation.of(builder -> builder.field(aggQuery.getField())));
                    break;
                }
                case DateHistogram -> {
                    containerBuilder = aggBuilder.dateHistogram(DateHistogramAggregation.of(builder -> builder.field(aggQuery.getField())));
                    break;
                }
                case Range -> {
                    containerBuilder = aggBuilder.range(RangeAggregation.of(builder -> builder.field(aggQuery.getField())));
                    break;
                }
                case Nested -> {
                    containerBuilder = aggBuilder.nested(NestedAggregation.of(builder -> builder.path(aggQuery.getField()))); // fixme
                    break;
                }
            }

            if( containerBuilder != null ){
                aggregationMap.put("agg_by_"+aggQuery.getField(), containerBuilder.build());
            }
        }
        return aggregationMap;
    }

}
