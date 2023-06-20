package com.sucl.springbootelasticsearch8.service;

import com.sucl.springbootelasticsearch8.core.query.Pager;
import com.sucl.springbootelasticsearch8.document.Novel;
import com.sucl.springbootelasticsearch8.helper.NovelQuerySupport;
import com.sucl.springbootelasticsearch8.helper.api.QidianBookQuery;
import com.sucl.springbootelasticsearch8.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.SearchHits;

import java.util.List;

/**
 * @author sucl
 * @date 2023/6/17 11:07
 * @since 1.0.0
 */
@Slf4j
@SpringBootTest
public class NovelServiceTests {

    @Autowired
    private NovelService novelService;

    @Test
    public void initNovels(){
        NovelQuerySupport.RequestParam requestParam = NovelQuerySupport.RequestParam.of().setType(QidianBookQuery.Type.XH.name()).setPageIndex(1);
        List<Novel> novels = NovelQuerySupport.queryWithOptions(requestParam);
        novelService.saveNovels(novels);
        log.info(">>> 获取数量：{}", novels.size());
    }

    @Test
    public void testAgg(){
        SearchHits<Novel> result = novelService.aggNovels("天下",
                new String[]{"title","description","content"},
                new String[]{"type","author"});
        log.info(">>> result: \n {}" , JsonUtils.toJson(result));
    }


    @Test
    public void testAggPage(){
        Page<Novel> result = novelService.aggPageNovels("天下",
                new String[]{"title","description","content"},
                new String[]{"type","author"},
                Pager.of(1, 5));
        log.info(">>> result: \n {}" , JsonUtils.toJson(result));
    }

}
