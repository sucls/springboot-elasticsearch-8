package com.sucl.springbootelasticsearch8.core.query;

import lombok.Data;
import org.springframework.util.NumberUtils;

import java.util.List;

/**
 * 复合查询
 * @author sucl
 * @date 2023/6/13 16:18
 * @since 1.0.0
 */
@Data
public class CompoundQuery implements Query{

    private Type type;

    private List<DslQuery> dslQueries;

    private Object value;

    public static CompoundQuery of(Type type, List<DslQuery> dslQueries, Object value){
        CompoundQuery compoundQuery = new CompoundQuery();
        compoundQuery.type = type;
        compoundQuery.dslQueries = dslQueries;
        compoundQuery.value = value;
        return compoundQuery;
    }

    public static CompoundQuery of(List<DslQuery> dslQueries){
        return of(null, dslQueries, null);
    }

    public double getDoubleValue(){
        if( value == null ){
            return 0;
        }
        if(Number.class.isAssignableFrom(this.value.getClass())){
            return ((Number) value).doubleValue();
        }
        return NumberUtils.parseNumber(this.value.toString(), Double.class);
    }

    public float getFloatValue() {
        if( value == null ){
            return 0f;
        }
        if(Number.class.isAssignableFrom(this.value.getClass())){
            return ((Number) value).floatValue();
        }
        return NumberUtils.parseNumber(this.value.toString(), Float.class);
    }

    /**
     * 复合查询类型
     */
    public enum Type{
        /**
         *
         */
        BOOL_QUERY,
        /**
         *
         */
        DIS_MAX,
        /**
         *
         */
        CONSTANT_SCORE,
        /**
         *
         */
        FUNCTION_SCORE;

    }
}
