package com.sucl.springbootelasticsearch8.dao;

import com.sucl.springbootelasticsearch8.core.dao.ElasticsearchDao;
import com.sucl.springbootelasticsearch8.document.Novel;

/**
 * @author sucl
 * @date 2023/6/6 19:03
 * @since 1.0.0
 */
public interface NovelDao extends ElasticsearchDao<Novel, String> {

}
