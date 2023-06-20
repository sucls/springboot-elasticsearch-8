package com.sucl.springbootelasticsearch8.document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Arrays;
import java.util.Date;
import java.util.StringJoiner;

/**
 * @author sucl
 * @date 2023/6/6 17:34
 * @since 1.0.0
 */
@JsonIgnoreProperties({"content"})
@Data
@Document(indexName = "index_novel")
public class Novel {

    @Id
    private String id;

    /**
     * title: text类型 用于检索
     * title.keyword : keyword类型用于聚合、排序等操作
     */
    @Field(type = FieldType.Auto)
    private String title;

    @Field(type = FieldType.Keyword)
    private String author;

    @Field(type = FieldType.Text)
    private String[] category;

    @Field(type = FieldType.Keyword)
    private String type;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String description;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String content;

    @Field(type = FieldType.Text)
    private String coverUrl;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private Date insertTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private Date updateTime;

    @Field(type = FieldType.Keyword)
    private String status;

    public Novel genId() {
        if(this.id == null){
            this.id = DigestUtils.md5Hex(String.join("-", this.title, this.author, this.type, this.status));
        }
        return this;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Novel.class.getSimpleName() + "[", "]")
                .add("title='" + title + "'")
                .add("author='" + author + "'")
                .add("type='" + type + "'")
                .add("category=" + Arrays.toString(category))
                .toString();
    }
}
