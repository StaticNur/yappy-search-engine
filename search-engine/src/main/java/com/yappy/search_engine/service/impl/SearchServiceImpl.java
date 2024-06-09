package com.yappy.search_engine.service.impl;

import com.yappy.search_engine.document.Video;
import com.yappy.search_engine.dto.SearchByEmbeddingDto;
import com.yappy.search_engine.mapper.SearchHitMapper;
import com.yappy.search_engine.service.SearchService;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class SearchServiceImpl implements SearchService {
    private final static String INDEX_NAME = "videos";
    private final ElasticsearchRestTemplate elasticsearchRestTemplate;
    private final RestHighLevelClient client;
    private final SearchHitMapper searchHitMapper;

    @Autowired
    public SearchServiceImpl(ElasticsearchRestTemplate elasticsearchRestTemplate,
                             RestHighLevelClient client, SearchHitMapper searchHitMapper) {
        this.elasticsearchRestTemplate = elasticsearchRestTemplate;
        this.client = client;
        this.searchHitMapper = searchHitMapper;
    }

    @Override
    public SearchHits<Video> searchVideosByEmbedding(SearchByEmbeddingDto embedding, int page, int size) {
        var script = """
            if (doc['embedding'].size() == 0) {
                return 0.0;
            } else {
                def score = cosineSimilarity(params.queryVector, 'embedding') + 1.0;
                if (Double.isNaN(score) || score < 0) {
                    return 0.0;
                } else {
                    return score;
                }
            }
        """;
        var params = Collections.singletonMap("queryVector", (Object) embedding.getEmbedding());
        var scriptType = ScriptType.INLINE;
        var language = "painless";

        var searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.scriptScoreQuery(QueryBuilders.matchAllQuery(),
                        new Script(scriptType, language, script, params)))
                .withPageable(PageRequest.of(page, size))
                .build();

        return elasticsearchRestTemplate.search(searchQuery, Video.class);
    }

    @Override
    public List<SearchHit<Video>> searchVideosByText(String query, int page, int size) {
        var searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery(query,
                        "title", "description", "tags"))
                .withPageable(PageRequest.of(page, size))
                .build();

        return elasticsearchRestTemplate.search(searchQuery, Video.class).getSearchHits();
    }

    @Override
    public List<Video> searchVideoLexicographic(String query, int page, int size) {
        SearchRequest searchRequest = new SearchRequest(INDEX_NAME);
        query = normalizeQuery(query);
        final int from = page <= 0 ? 0 : page * size;
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .from(from)
                .size(size);
        String[] queryParts = query.split(" ");
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        for (String part : queryParts) {
            boolQueryBuilder.should(QueryBuilders.wildcardQuery("title", "*" + part + "*"));
            boolQueryBuilder.should(QueryBuilders.wildcardQuery("description", "*" + part + "*"));
            boolQueryBuilder.should(QueryBuilders.wildcardQuery("tags", "*" + part + "*"));
        }
        searchSourceBuilder.query(boolQueryBuilder);

        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse;
        try {
            searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return extractVideosFromResponse(searchResponse);
    }

    private List<Video> extractVideosFromResponse(SearchResponse searchResponse) {
        List<Video> videos = new ArrayList<>();
        for (org.elasticsearch.search.SearchHit hit : searchResponse.getHits().getHits()) {
            Video video = searchHitMapper.getVideo(hit);
            videos.add(video);
        }
        Collections.reverse(videos);
        return videos;
    }

    //TODO какие знаки можно не убирать?
    private String normalizeQuery(String query) {
        query = query.replaceAll("[,;!&$?№~@#%^*+:<>=]", "")
                .replaceAll("[-.]", " ")
                .replaceAll("/"," ")
                .replaceAll("[\\s]+", " ");
        return query.trim();
    }
}
