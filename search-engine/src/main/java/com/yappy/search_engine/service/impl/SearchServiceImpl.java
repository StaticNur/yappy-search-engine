package com.yappy.search_engine.service.impl;

import com.yappy.search_engine.document.Video;
import com.yappy.search_engine.dto.SearchByEmbeddingDto;
import com.yappy.search_engine.dto.SearchRequestDto;
import com.yappy.search_engine.dto.VideoSearchResult;
import com.yappy.search_engine.helper.Indices;
import com.yappy.search_engine.mapper.SearchHitMapper;
import com.yappy.search_engine.out.service.ApiClient;
import com.yappy.search_engine.service.SearchService;
import com.yappy.search_engine.search.SearchUtil;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.*;
import org.elasticsearch.index.query.functionscore.ScriptScoreQueryBuilder;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class SearchServiceImpl implements SearchService {
    private final RestHighLevelClient client;
    private final SearchHitMapper searchHitMapper;
    private final ApiClient apiClient;

    @Autowired
    public SearchServiceImpl(RestHighLevelClient client, SearchHitMapper searchHitMapper,
                             ApiClient apiClient) {
        this.client = client;
        this.searchHitMapper = searchHitMapper;
        this.apiClient = apiClient;
    }

    @Override
    public VideoSearchResult searchVideosByEmbedding(SearchByEmbeddingDto embedding, int page, int size) {
        SearchRequest searchRequest = new SearchRequest(Indices.VIDEOS_INDEX);

        double[] embeddingQuery = apiClient.getEmbedding(embedding.getQuery());
        System.out.println("Query text:"+embedding.toString());
        System.out.println("Query embedding: ["+embeddingQuery[0]+"...] length="+embeddingQuery.length);

        ScriptType scriptType = ScriptType.INLINE;
        String language = "painless";
        Map<String, Object> params = Collections.singletonMap("queryVector", (Object) embeddingQuery);
        final int from = page <= 0 ? 0 : page * size;
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .from(from)
                .size(size);

        ScriptScoreQueryBuilder scriptScoreQueryBuilderAudio
                = getScriptBuilderAudio(scriptType, language, params, embedding.getBoostEmbeddingAudio());

        ScriptScoreQueryBuilder scriptScoreQueryBuilderVisual
                = getScriptBuilderVisual(scriptType, language, params, embedding.getBoostDescriptionVisual());

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.should(QueryBuilders
                .matchQuery("descriptionUser", embedding.getQuery())
                .fuzziness(Fuzziness.fromEdits(embedding.getCoefficientOfCoincidenceDescriptionUser()))
                .prefixLength(embedding.getMinimumPrefixLengthDescriptionUser())
                .maxExpansions(embedding.getMaximumNumberOfMatchOptionsDescriptionUser())
                .boost(embedding.getBoostDescriptionUser()));

        String[] queryParts = embedding.getQuery().split(" ");
        BoolQueryBuilder tagsQueryBuilder = QueryBuilders.boolQuery();
        for (String part : queryParts) {
            if (part.startsWith("#")) {
                part = part.replace("#", "");
                tagsQueryBuilder.should(QueryBuilders.matchQuery("tags", part)
                        .boost(embedding.getBoostTags()));
            } else {
                tagsQueryBuilder.should(QueryBuilders.fuzzyQuery("tags", part)
                        .fuzziness(Fuzziness.fromEdits(embedding.getCoefficientOfCoincidenceTag()))  // Установка коэффициента совпадения в 2
                        .prefixLength(embedding.getMaximumNumberOfMatchOptionsTag())                    // 1 Минимальная длина префикса, которая должна быть неизменной
                        .maxExpansions(embedding.getMaximumNumberOfMatchOptionsTag()));                // 10 Максимальное количество вариантов совпадения
            }
        }

        BoolQueryBuilder combinedQueryBuilder = QueryBuilders.boolQuery()
                .should(scriptScoreQueryBuilderAudio)
                .should(scriptScoreQueryBuilderVisual)
                .should(boolQueryBuilder)
                .should(tagsQueryBuilder);


        searchSourceBuilder.query(combinedQueryBuilder);
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
                tagsQueryBuilder.should(QueryBuilders.matchQuery("tags", part)
                        .boost(2.0f));
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
    public VideoSearchResult getRecommendations() {
        final SearchRequest request = SearchUtil.buildSearchRecommendationRequest(Indices.VIDEOS_INDEX);
        return searchInternal(request);
    }

    @Override
    public VideoSearchResult searchWithFilter(SearchRequestDto dto, String date) {
        SearchRequest request;
        System.out.println(dto.toString());
        if (dto.getTypeSearch().equals("embedding")) {
            SearchByEmbeddingDto searchByEmbeddingDto = new SearchByEmbeddingDto(dto.getQuery(),
                    2,
                    2,
                    50,
                    2,
                    3,
                    5,
                    1,
                    1,
                    1,
                    1,
                    1,
                    1,
                    1);
            return searchVideosByEmbedding(searchByEmbeddingDto, dto.getPage(), dto.getSize());
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

    private ScriptScoreQueryBuilder getScriptBuilderAudio(ScriptType scriptType, String language,
                                                          Map<String, Object> params, float boost){
        var script = """
                    if (doc['embedding_audio'].size() == 0) {
                        return 0.0;
                    } else {
                        def score = cosineSimilarity(params.queryVector, 'embedding_audio') + 1.0;
                        if (Double.isNaN(score) || score < 0) {
                            return 0.0;
                        } else {
                            return score;
                        }
                    }
                """;
        return QueryBuilders.scriptScoreQuery(
                QueryBuilders.matchAllQuery(),
                new Script(scriptType, language, script, params)
        ).boost(boost);
    }

    private ScriptScoreQueryBuilder getScriptBuilderVisual(ScriptType scriptType, String language,
                                                          Map<String, Object> params, float boost){
        var script = """
                    if (doc['embedding_visual'].size() == 0) {
                        return 0.0;
                    } else {
                        def score = cosineSimilarity(params.queryVector, 'embedding_visual') + 1.0;
                        if (Double.isNaN(score) || score < 0) {
                            return 0.0;
                        } else {
                            return score;
                        }
                    }
                """;
        return QueryBuilders.scriptScoreQuery(
                QueryBuilders.matchAllQuery(),
                new Script(scriptType, language, script, params)
        ).boost(boost);
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
