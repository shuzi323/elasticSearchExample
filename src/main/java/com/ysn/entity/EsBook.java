package com.ysn.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @Author Yang ShuNing
 * @Date 2020/7/3
 */

@Document(indexName = "test")
public class EsBook {
    @Id
    private Integer id;

    @Field(analyzer = "ik_smart", type = FieldType.Keyword)
    private String name;
    @Field(analyzer = "ik_max_word")
    private String author;

    @Field(analyzer = "ik_smart", type = FieldType.Text)
    private String content;

    private Long voiceCount;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Long getVoiceCount() {
        return voiceCount;
    }

    public void setVoiceCount(Long voiceCount) {
        this.voiceCount = voiceCount;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "EsBook{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", content='" + content + '\'' +
                ", voiceCount=" + voiceCount +
                '}';
    }
}
