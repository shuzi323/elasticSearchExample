package com.ysn.repository;

import com.ysn.entity.EsBook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * @Author Yang ShuNing
 * @Date 2020/7/3
 */
public interface BookRepository extends ElasticsearchRepository<EsBook, Integer> {
    List<EsBook> findAllByName(String name);
    Page<EsBook> findAllByName(String name, Pageable pageable);
}
