package com.ysn.repository;

import com.ysn.entity.EsBook;
import org.elasticsearch.common.lucene.search.function.FieldValueFactorFunction;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchHitsIterator;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author Yang ShuNing
 * @Date 2020/7/28
 */
//@RunWith(SpringRunner.class)
@SpringBootTest
public class BookRepositoryTest {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;
//    @Qualifier("elasticsearchTemplate")
    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Test
    public void saveTest(){
        EsBook book = new EsBook();
        book.setId(3);
        book.setName("三国演义");
        book.setAuthor("罗贯中");
        book.setContent("");
        book.setVoiceCount(10L);
        System.out.println(elasticsearchRestTemplate.save(book));
//        IndexQuery indexQuery = new IndexQueryBuilder()
//                .withId(book.getId().toString())
//                .withObject(book)
//                .build();
//        System.out.println(elasticsearchOperations.index(indexQuery, IndexCoordinates.of("test")));
    }

    @Test
    public void searchTest(){
        Query query = new StringQuery("三国");
        query.setPreference("三");
        query.addFields("name", "author");
        query.setPageable(PageRequest.of(1, 10));

        Query searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("记","name", "author"))
                .withPageable(PageRequest.of(0, 10))
                .withHighlightFields(new HighlightBuilder.Field("name"), new HighlightBuilder.Field("author"))
                .build();


        bookRepository.findAll().forEach(System.out::println);
        System.out.println();
        bookRepository.findAllByName("记").forEach(System.out::println);
        System.out.println();
        System.out.println(bookRepository.findAllByName("记", PageRequest.of(1, 10)));
        System.out.println();
        SearchHits<EsBook> o = elasticsearchRestTemplate.search(searchQuery, EsBook.class, IndexCoordinates.of("test"));
        System.out.println(o);
        o.get().forEach(System.out::println);
        System.out.println(o.getSearchHits());

//        elasticsearchOperations.multiSearch(query, EsBook.class, IndexCoordinates.of("test"))
//        System.out.println(elasticsearchRestTemplate.search(query, EsBook.class, IndexCoordinates.of("test")));
    }

    @Test
    public void searchAllTest(){
        bookRepository.findAll().forEach(System.out::println);
    }

    @Test
    public void scoreTest(){
        ScoreFunctionBuilder<?> scoreFunctionBuilder = ScoreFunctionBuilders
                .fieldValueFactorFunction("voiceCount")
                .modifier(FieldValueFactorFunction.Modifier.LN2P)
                .missing(1)
                .factor(0.001F);

        Query searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("\"三国演义\"","name", "author", "content"))
                .withPageable(PageRequest.of(0, 10))
                .withHighlightFields(new HighlightBuilder.Field("name"),
                        new HighlightBuilder.Field("author"),
                        new HighlightBuilder.Field("content"))
                .build();

        FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders
                .functionScoreQuery(QueryBuilders.multiMatchQuery("美甘甘似玉液琼浆","name", "author", "content"),
                        scoreFunctionBuilder);
        Query queryFunc = new NativeSearchQueryBuilder()
                .withQuery(functionScoreQueryBuilder)
                .withPageable(PageRequest.of(0, 10))
                .withHighlightFields(new HighlightBuilder.Field("name"),
                        new HighlightBuilder.Field("author"),
                        new HighlightBuilder.Field("content").fragmentSize(10).numOfFragments(3))
                .withMinScore(0.5F)
                .build();


        SearchHits<EsBook> o = elasticsearchRestTemplate.search(queryFunc, EsBook.class, IndexCoordinates.of("test"));
        System.out.println(o);
        o.get().forEach(searchHits -> {
            searchHits.getContent().setContent(null);
            System.out.println(searchHits);
        });
    }

    @Test
    public void updateTest(){
        Map<String, Object> data = new HashMap<>();
        data.put("content", "");
        UpdateQuery updateQuery = UpdateQuery.builder("2").withDocument(Document.from(data)).build();
        elasticsearchRestTemplate.update(updateQuery, IndexCoordinates.of("test"));
    }
}