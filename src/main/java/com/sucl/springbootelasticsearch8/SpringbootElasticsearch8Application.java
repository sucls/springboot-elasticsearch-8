package com.sucl.springbootelasticsearch8;

import com.sucl.springbootelasticsearch8.core.dao.BaseElasticsearchRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@SpringBootApplication
@EnableElasticsearchRepositories(basePackages = "com.sucl.springbootelasticsearch8.dao", repositoryBaseClass = BaseElasticsearchRepository.class)
public class SpringbootElasticsearch8Application {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootElasticsearch8Application.class, args);
    }

}
