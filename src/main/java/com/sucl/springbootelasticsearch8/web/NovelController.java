package com.sucl.springbootelasticsearch8.web;

import com.sucl.springbootelasticsearch8.core.query.Pager;
import com.sucl.springbootelasticsearch8.document.Novel;
import com.sucl.springbootelasticsearch8.helper.NovelQuerySupport;
import com.sucl.springbootelasticsearch8.service.NovelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author sucl
 * @date 2023/6/6 19:53
 * @since 1.0.0
 */
@RestController
@RequestMapping("/novel")
public class NovelController {

    @Autowired
    private NovelService novelService;

    /**
     * 初始化
     * @param requestParam
     * @return
     */
    @PostMapping("/init")
    public int init(@RequestBody NovelQuerySupport.RequestParam requestParam){
        List<Novel> novels = NovelQuerySupport.queryWithOptions(requestParam);
        novelService.saveNovels(novels);
        return novels.size();
    }

    /**
     * 根据主键查询
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Novel getNovel(@PathVariable("id") String id){
        return novelService.getNovel(id);
    }

    /**
     * 保存
     * @param novel
     * @return
     */
    @PostMapping
    public Novel saveNovels(@RequestBody Novel novel){
        return novelService.saveNovel(novel);
    }

    /**
     * 批量保存
     * @param novels
     * @return
     */
    @PostMapping("/batch")
    public List<Novel> saveNovels(@RequestBody List<Novel> novels){
        return novelService.saveNovels(novels);
    }

    /**
     * 批量查询
     * @return
     */
    @GetMapping("/batch")
    public List<Novel> getNovels(){
        return novelService.getNovels();
    }

    /**
     * 根据关键字对指定字段检索
     * @param keyword
     * @param fields
     * @return
     */
    @PostMapping("/search")
    public List<Novel> searchNovels(@RequestParam String keyword,
                                    @RequestParam String[] fields){
        return novelService.searchNovels(keyword, fields);
    }

    /**
     * 基于id查询指定字段相似的数据
     * @param page
     * @return
     */
    @PostMapping("/pageSimilar")
    public Page<Novel> getPageSimilarNovel(Novel novel, @RequestParam String[] fields, Pager pager){
        return novelService.getPageSimilarNovel(novel, fields, pager);
    }

    /**
     * 聚合数据无法序列化成json，需要处理
     * @param keyword
     * @param fields
     * @param groupFields
     * @return
     */
    @PostMapping("/agg")
    public SearchHits<Novel> aggNovels(@RequestParam String keyword,
                                       @RequestParam String[] fields,
                                       @RequestParam String[] groupFields){
        return novelService.aggNovels(keyword, fields, groupFields);
    }

    /**
     *
     * @param keyword
     * @param fields
     * @param groupFields
     * @param pageRequest
     * @return
     */
    @PostMapping("/aggPage")
    public Page<Novel> aggPageNovels(@RequestParam String keyword,
                                     @RequestParam String[] fields,
                                     @RequestParam String[] groupFields,
                                     Pager pager){
        return novelService.aggPageNovels(keyword, fields, groupFields, pager);
    }

    /**
     * 根据主键删除
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public void deleteNovel(@PathVariable String id){
        novelService.deleteNovel(id);
    }
}
