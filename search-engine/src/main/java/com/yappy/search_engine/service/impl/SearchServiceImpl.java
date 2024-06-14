package com.yappy.search_engine.service.impl;

import com.yappy.search_engine.document.Video;
import com.yappy.search_engine.dto.SearchByEmbeddingDto;
import com.yappy.search_engine.dto.SearchRequestDto;
import com.yappy.search_engine.dto.VideoSearchResult;
import com.yappy.search_engine.helper.Indices;
import com.yappy.search_engine.mapper.SearchHitMapper;
import com.yappy.search_engine.service.SearchService;
import com.yappy.search_engine.search.SearchUtil;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.Fuzziness;
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
                        "title", "descriptionUser", "tags"))
                .withPageable(PageRequest.of(page, size))
                .build();

        return elasticsearchRestTemplate.search(searchQuery, Video.class).getSearchHits();
    }

    @Override
    public VideoSearchResult searchVideoLexicographic(String query, int page, int size) {
        SearchRequest searchRequest = new SearchRequest(Indices.VIDEOS_INDEX);
        System.out.println("query:" + query);
        query = normalizeQuery(query);
        System.out.println("normalizeQuery:" + query);
        final int from = page <= 0 ? 0 : page * size;
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .from(from)
                .size(size);

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        boolQueryBuilder.should(QueryBuilders
                .matchQuery("title", query)
                .fuzziness(Fuzziness.AUTO));
        boolQueryBuilder.should(QueryBuilders
                .matchQuery("descriptionUser", query)
                .fuzziness(Fuzziness.AUTO));

        String[] queryParts = query.split(" ");
        BoolQueryBuilder tagsQueryBuilder = QueryBuilders.boolQuery();
        for (String part : queryParts) {
            System.out.println("tags:" + part);
            if (part.startsWith("#")) {
                part = part.replace("#", "");
                tagsQueryBuilder.should(QueryBuilders.termQuery("tags", part).boost(2.0f));
            } else {
                tagsQueryBuilder.should(QueryBuilders.fuzzyQuery("tags", part).fuzziness(Fuzziness.AUTO));
            }
        }
        boolQueryBuilder.should(tagsQueryBuilder);

        searchSourceBuilder.query(boolQueryBuilder);
        searchRequest.source(searchSourceBuilder);


        SearchResponse searchResponse;
        try {
            searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        long totalHits = searchResponse.getHits().getTotalHits().value;
        List<Video> videos = extractVideosFromResponse(searchResponse);

        return new VideoSearchResult(videos, totalHits);
    }


    @Override
    public VideoSearchResult getVideoByDate(final String date) {
        final SearchRequest request = SearchUtil.buildSearchRequest(
                Indices.VIDEOS_INDEX,
                "created",
                date
        );
        return searchInternal(request);
    }

    @Override
    public VideoSearchResult search(SearchRequestDto dto, String date) {
        SearchRequest request;
        System.out.println(dto.toString());
        if (dto.getTypeSearch().equals("embedding")) {
            request = SearchUtil.buildSearchRequest(
                    Indices.VIDEOS_INDEX,
                    dto, date
            );
        }else {
            request = SearchUtil.buildSearchRequest(
                    Indices.VIDEOS_INDEX,
                    dto, date);
        }
        return searchInternal(request);
    }

    private VideoSearchResult searchInternal(final SearchRequest request) {
        if (request == null) {
            return new VideoSearchResult(Collections.emptyList(), 0);
        }
        try {
            final SearchResponse response = client.search(request, RequestOptions.DEFAULT);

            long totalHits = response.getHits().getTotalHits().value;
            List<Video> videos = extractVideosFromResponse(response);

            return new VideoSearchResult(videos, totalHits);
        } catch (Exception e) {
            return new VideoSearchResult(Collections.emptyList(), 0);
        }
    }

    private List<Video> extractVideosFromResponse(SearchResponse searchResponse) {
        List<Video> videos = new ArrayList<>();
        for (org.elasticsearch.search.SearchHit hit : searchResponse.getHits().getHits()) {
            Video video = searchHitMapper.getVideo(hit);
            videos.add(video);
        }
        return videos;
    }

    //TODO какие знаки можно не убирать?
    private String normalizeQuery(String query) {
        query = query.replaceAll("[,;!&$?№~@%^*+:<>=]", "")
                .replaceAll("[-._]", " ")
                .replaceAll("/", " ")
                .replaceAll("[\\s]+", " ");
        return query.trim();
    }
}
