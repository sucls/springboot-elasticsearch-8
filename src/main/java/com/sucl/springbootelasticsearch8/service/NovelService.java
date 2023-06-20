package com.sucl.springbootelasticsearch8.service;

import com.sucl.springbootelasticsearch8.core.query.AggQuery;
import com.sucl.springbootelasticsearch8.core.query.CompoundQuery;
import com.sucl.springbootelasticsearch8.core.query.DslQuery;
import com.sucl.springbootelasticsearch8.core.query.Pager;
import com.sucl.springbootelasticsearch8.dao.NovelDao;
import com.sucl.springbootelasticsearch8.document.Novel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author sucl
 * @date 2023/6/6 19:31
 * @since 1.0.0
 */
@Service
public class NovelService {

    private NovelDao novelDao;

    public NovelService(NovelDao novelDao) {
        this.novelDao = novelDao;
    }

    /**
     *
     * @param novel
     * @return
     */
    public Novel saveNovel(Novel novel){
        return novelDao.save( configureNovel(novel) );
    }

    private Novel configureNovel(Novel novel){
        if(!StringUtils.hasText(novel.getId())){
            novel.setId(UUID.randomUUID().toString());
        }
        if( novel.getInsertTime() == null ){
            novel.setInsertTime(new Date());
        }
        novel.setUpdateTime(new Date());
        return novel;
    }

    /**
     *
     * @param novels
     * @return
     */
    public List<Novel> saveNovels(List<Novel> novels){
        List<Novel> savedNovels = new ArrayList<>();
        novels.forEach(this::configureNovel);
        novelDao.saveAll(novels).forEach(savedNovels::add);
        return savedNovels;
    }

    /**
     *
     * @param id
     * @return
     */
    public Novel getNovel(String id){
        return novelDao.findById(id).orElse(null);
    }

    /**
     *
     * @return
     */
    public List<Novel> getNovels(){
        List<Novel> novels = new ArrayList<>();
        novelDao.findAll().forEach(novels::add);
        return novels;
    }

    public List<Novel> searchNovels(String keyword, String[] fields) {
        DslQuery dslQuery = DslQuery.of(DslQuery.Type.MULTI_MATCH, String.join(",",fields), keyword);
        return novelDao.commonQuery(dslQuery, null);
    }

    /**
     *
     * @param pageable
     * @return
     */
    public Page<Novel> getPageNovel(Pageable pageable){
        return novelDao.findAll(pageable);
    }

    /**
     *
     * @param novel
     * @param fields
     * @param pageable
     * @return
     */
    public Page<Novel> getPageSimilarNovel(Novel novel, String[] fields, Pager pager){
        return novelDao.searchSimilar(novel, fields, PageRequest.of(pager.getPageIndex(), pager.getPageSize()));
    }

    /**
     *
     * @param id
     */
    public void deleteNovel(String id){
        novelDao.deleteById(id);
    }

    /**
     *
     * @param keyword
     * @param fields
     * @param groupFields
     * @param pager
     * @return
     */
    public Page<Novel> aggPageNovels(String keyword, String[] fields, String[] groupFields, Pager pager) {
        CompoundQuery compoundQuery = CompoundQuery.of(Arrays.asList(DslQuery.of(DslQuery.Type.MULTI_MATCH, String.join(",",fields), keyword)));
        List<AggQuery> aggQueries = Arrays.stream(groupFields).map(field->AggQuery.of(AggQuery.Type.Terms, field)).collect(Collectors.toList());
        return novelDao.aggregationPage(compoundQuery, aggQueries, pager);
    }

    /**
     *
     * @param keyword
     * @param fields
     * @param groupFields
     * @return
     */
    public SearchHits<Novel> aggNovels(String keyword, String[] fields, String[] groupFields) {
        CompoundQuery compoundQuery = CompoundQuery.of(Arrays.asList(DslQuery.of(DslQuery.Type.MULTI_MATCH, String.join(",",fields), keyword)));
        List<AggQuery> aggQueries = Arrays.stream(groupFields).map(field->AggQuery.of(AggQuery.Type.Terms, field)).collect(Collectors.toList());
        return novelDao.aggregation(compoundQuery, aggQueries);
    }

}
