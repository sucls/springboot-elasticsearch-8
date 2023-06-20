package com.sucl.springbootelasticsearch8;

import com.sucl.springbootelasticsearch8.service.NovelService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringbootElasticsearch8ApplicationTests {

    @Autowired
    private NovelService novelService;

    @Test
    void contextLoads() {
        System.out.println(novelService);
    }

}
