package com.sucl.springbootelasticsearch8.core.query;

import org.springframework.boot.context.properties.PropertyMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 通用查询支持
 * @author sucl
 * @date 2023/6/16 8:54
 * @since 1.0.0
 */
public abstract class QuerySupport implements Query{

    private Map<String,Object> options = new HashMap<>();

    /**
     *
     * @return
     */
    public Map<String, Object> buildOptions() {
        Properties properties = new Properties();
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
//         map.from(Supplier).to(Consumer)
        return properties;
    }

    /**
     *
     * @param name
     * @param type
     * @param consumer
     * @return
     * @param <IN>
     * @param <OUT>
     */
    public <IN,OUT> QuerySupport option(String name, Class<IN> type, Function<IN, OUT> consumer){
        Function<Object,IN> valueBuilder = val-> type.isInstance(val) ? type.cast(val) : null;
        return option(name, valueBuilder,consumer);
    }

    /**
     *
     * @param name
     * @param converter
     * @param consumer
     * @return
     * @param <IN>
     * @param <OUT>
     */
    public <IN,OUT> QuerySupport option(String name, Converter<Object,IN> converter, Function<IN, OUT> consumer){
        Function<Object,IN> valueBuilder = val-> converter!=null?  converter.convert(val): null;
        return option(name, valueBuilder,consumer);
    }

    /**
     *
     * @param name
     * @param valueBuilder 从options将值转换成目标类型
     * @param consumer
     * @return
     * @param <IN>
     * @param <OUT>
     */
    public <IN,OUT> QuerySupport option(String name, Function<Object,IN> valueBuilder, Function<IN, OUT> consumer){
        Object value = options.get(name);
        OUT out = null;
        if( consumer != null && valueBuilder != null){
            out = consumer.apply(valueBuilder.apply(value));
        }
        return this;
    }

    private static class Properties extends HashMap<String, Object> {

        <V> Consumer<V> in(String key) {
            return (value) -> put(key, value);
        }

        Properties with(Map<String, String> properties) {
            putAll(properties);
            return this;
        }

    }

    @FunctionalInterface
    public interface Converter<IN,OUT>{

        OUT convert(IN in);

    }

    public enum OptionConverter implements Converter{
        analyzer{
            @Override
            public String convert(Object option) {
                return  super.convert(option);
            }
        },
        fuzziness{
            @Override
            public String convert(Object option) {
                return super.convert(option);
            }
        };

        @Override
        public String convert(Object option) {
            return Objects.toString(option, null);
        }
    }

}
