package com.sucl.springbootelasticsearch8.core.query;

import java.util.Collections;
import java.util.Map;

/**
 * @author sucl
 * @date 2023/6/13 16:43
 * @since 1.0.0
 */
public interface Query {

    /**
     *
     * @return
     */
    default String[] getSource(){
        return null;
    }

    /**
     *
     * @return
     */
    default Map<String,Object> getOptions(){
        return Collections.EMPTY_MAP;
    }

}
