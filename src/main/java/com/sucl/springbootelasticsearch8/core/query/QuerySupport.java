package com.sucl.springbootelasticsearch8.core.query;

import org.springframework.boot.context.properties.PropertyMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 通用查询支持
 * @author sucl
 * @date 2023/6/16 8:54
 * @since 1.0.0
 */
public abstract class QuerySupport implements Query{

    /**
     *
     * @return
     */
    public Map<String, Object> buildOptions() {
        Properties properties = new Properties();
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        // map.from(Supplier).to(Consumer)
        return properties;
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

}
