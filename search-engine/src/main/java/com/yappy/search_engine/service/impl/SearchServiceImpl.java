package com.yappy.search_engine.service.impl;

import com.yappy.search_engine.document.Video;
import com.yappy.search_engine.dto.SearchByParameterDto;
import com.yappy.search_engine.dto.SearchRequestDto;
import com.yappy.search_engine.dto.VideoSearchResult;
import com.yappy.search_engine.helper.Indices;
import com.yappy.search_engine.mapper.SearchHitMapper;
import com.yappy.search_engine.mapper.VideoMapper;
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
    public VideoSearchResult searchVideoText(String query, int page, int size) {
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

        return new VideoSearchResult(VideoMapper.buildVideoResponse(videos), totalHits);
    }

    @Override
    public VideoSearchResult searchVideoFullText(SearchByParameterDto searchByParameterDto, int page, int size) {
        SearchRequest searchRequest = new SearchRequest(Indices.VIDEOS_INDEX);

        System.out.println("Query text:" + searchByParameterDto.toString());

        final int from = page <= 0 ? 0 : page * size;
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .from(from)
                .size(size);

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.should(QueryBuilders
                .matchQuery("descriptionUser", searchByParameterDto.getQuery())
                .fuzziness(Fuzziness.fromEdits(searchByParameterDto.getCoefficientOfCoincidenceDescriptionUser()))
                .prefixLength(searchByParameterDto.getMinimumPrefixLengthDescriptionUser())
                .maxExpansions(searchByParameterDto.getMaximumNumberOfMatchOptionsDescriptionUser())
                .boost(searchByParameterDto.getBoostDescriptionUser()));

        boolQueryBuilder.should(QueryBuilders
                .matchQuery("transcriptionAudio", searchByParameterDto.getQuery())
                .fuzziness(Fuzziness.fromEdits(searchByParameterDto.getCoefficientOfCoincidenceAudio()))
                .prefixLength(searchByParameterDto.getMinimumPrefixLengthAudio())
                .maxExpansions(searchByParameterDto.getMaximumNumberOfMatchOptionsAudio())
                .boost(searchByParameterDto.getBoostTranscriptionAudio()));

        boolQueryBuilder.should(QueryBuilders
                .matchQuery("descriptionVisual", searchByParameterDto.getQuery())
                .fuzziness(Fuzziness.fromEdits(searchByParameterDto.getCoefficientOfCoincidenceVisual()))
                .prefixLength(searchByParameterDto.getMinimumPrefixLengthVisual())
                .maxExpansions(searchByParameterDto.getMaximumNumberOfMatchOptionsVisual())
                .boost(searchByParameterDto.getBoostDescriptionVisual()));

        String[] queryParts = searchByParameterDto.getQuery().split(" ");
        BoolQueryBuilder tagsQueryBuilder = QueryBuilders.boolQuery();
        for (String part : queryParts) {
            if (part.startsWith("#")) {
                part = part.replace("#", "");
                tagsQueryBuilder.should(QueryBuilders.matchQuery("tags", part)
                        .boost(searchByParameterDto.getBoostTags()));
            } else {
                tagsQueryBuilder.should(QueryBuilders.fuzzyQuery("tags", part)
                        .fuzziness(Fuzziness.fromEdits(searchByParameterDto.getCoefficientOfCoincidenceTag()))  // Установка коэффициента совпадения (возможны 0, 1, 2 и 3)
                        .prefixLength(searchByParameterDto.getMaximumNumberOfMatchOptionsTag())                    // Минимальная длина префикса, которая должна быть неизменной
                        .maxExpansions(searchByParameterDto.getMaximumNumberOfMatchOptionsTag()));                // Максимальное количество вариантов совпадения
            }
        }

        BoolQueryBuilder combinedQueryBuilder = QueryBuilders.boolQuery()
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

        return new VideoSearchResult(VideoMapper.buildVideoResponse(videos), totalHits);
    }

    @Override
    public VideoSearchResult searchVideosByEmbedding(SearchByParameterDto embedding, int page, int size) {
        SearchRequest searchRequest = new SearchRequest(Indices.VIDEOS_INDEX);

        double[] embeddingQuery = apiClient.getEmbedding(embedding.getQuery());
        System.out.println("Query text:" + embedding.toString());
        System.out.println("Query embedding: [" + embeddingQuery[0] + "...] length=" + embeddingQuery.length);

        final int from = page <= 0 ? 0 : page * size;
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .from(from)
                .size(size);

        ScriptScoreQueryBuilder scriptScoreQueryBuilderUserDescription
                = getScriptBuilder("embeddingUserDescription", embeddingQuery, embedding.getBoostDescriptionUser());

        ScriptScoreQueryBuilder scriptScoreQueryBuilderAudio
                = getScriptBuilder("embeddingAudio", embeddingQuery, embedding.getBoostEmbeddingAudio());

        ScriptScoreQueryBuilder scriptScoreQueryBuilderVisual
                = getScriptBuilder("embeddingVisual", embeddingQuery, embedding.getBoostDescriptionVisual());

        BoolQueryBuilder combinedQueryBuilder = QueryBuilders.boolQuery()
                .should(scriptScoreQueryBuilderUserDescription)
                .should(scriptScoreQueryBuilderAudio)
                .should(scriptScoreQueryBuilderVisual);

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

        return new VideoSearchResult(VideoMapper.buildVideoResponse(videos), totalHits);
    }

    @Override
    public VideoSearchResult searchVideosByCombine(SearchByParameterDto dto, int page, int size, String date) {
        SearchRequest searchRequest = new SearchRequest(Indices.VIDEOS_INDEX);

        double[] embeddingQuery = apiClient.getEmbedding(dto.getQuery());
        System.out.println("Query text:" + dto.toString());
        System.out.println("Query embedding: [" + embeddingQuery[0] + "...] length=" + embeddingQuery.length);

        final int from = page <= 0 ? 0 : page * size;
        QueryBuilder dateQuery = QueryBuilders.rangeQuery("created").gte(date);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .from(from)
                .size(size)
                .postFilter(dateQuery);

        ScriptScoreQueryBuilder scriptScoreQueryBuilderUserDescription
                = getScriptBuilder("embeddingUserDescription", embeddingQuery, dto.getBoostEmbeddingUserDescription());

        ScriptScoreQueryBuilder scriptScoreQueryBuilderAudio
                = getScriptBuilder("embeddingAudio", embeddingQuery, dto.getBoostEmbeddingAudio());

        ScriptScoreQueryBuilder scriptScoreQueryBuilderVisual
                = getScriptBuilder("embeddingVisual", embeddingQuery, dto.getBoostEmbeddingVisual());

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.should(QueryBuilders
                .matchQuery("descriptionUser", dto.getQuery())
                .fuzziness(Fuzziness.fromEdits(dto.getCoefficientOfCoincidenceDescriptionUser()))
                .prefixLength(dto.getMinimumPrefixLengthDescriptionUser())
                .maxExpansions(dto.getMaximumNumberOfMatchOptionsDescriptionUser())
                .boost(dto.getBoostDescriptionUser()));

        boolQueryBuilder.should(QueryBuilders
                .matchQuery("transcriptionAudio", dto.getQuery())
                .fuzziness(Fuzziness.fromEdits(dto.getCoefficientOfCoincidenceAudio()))
                .prefixLength(dto.getMinimumPrefixLengthAudio())
                .maxExpansions(dto.getMaximumNumberOfMatchOptionsAudio())
                .boost(dto.getBoostTranscriptionAudio()));

        boolQueryBuilder.should(QueryBuilders
                .matchQuery("descriptionVisual", dto.getQuery())
                .fuzziness(Fuzziness.fromEdits(dto.getCoefficientOfCoincidenceVisual()))
                .prefixLength(dto.getMinimumPrefixLengthVisual())
                .maxExpansions(dto.getMaximumNumberOfMatchOptionsVisual())
                .boost(dto.getBoostDescriptionVisual()));

        String[] queryParts = dto.getQuery().split(" ");
        BoolQueryBuilder tagsQueryBuilder = QueryBuilders.boolQuery();
        for (String part : queryParts) {
            if (part.startsWith("#")) {
                part = part.replace("#", "");
                tagsQueryBuilder.should(QueryBuilders.matchQuery("tags", part)
                        .boost(dto.getBoostTags()));
            } else {
                tagsQueryBuilder.should(QueryBuilders.fuzzyQuery("tags", part)
                        .fuzziness(Fuzziness.fromEdits(dto.getCoefficientOfCoincidenceTag()))  // Установка коэффициента совпадения
                        .prefixLength(dto.getMinimumPrefixLengthTag())                    // Минимальная длина префикса, которая должна быть неизменной
                        .maxExpansions(dto.getMaximumNumberOfMatchOptionsTag()));                // Максимальное количество вариантов совпадения
            }
        }

        BoolQueryBuilder combinedQueryBuilder = QueryBuilders.boolQuery()
                .should(scriptScoreQueryBuilderUserDescription)
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

        return new VideoSearchResult(VideoMapper.buildVideoResponse(videos), totalHits);
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
            SearchByParameterDto searchByParameterDto = new SearchByParameterDto(dto.getQuery(),
                    1,
                    1,
                    50,
                    0,
                    2,
                    50,
                    0,
                    2,
                    50,
                    1,
                    2,
                    50,
                    1,
                    0,
                    1,
                    1,
                    2,
                    10,
                    4);
            return searchVideosByCombine(searchByParameterDto, dto.getPage(), dto.getSize(), date);
        } else {
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

            return new VideoSearchResult(VideoMapper.buildVideoResponse(videos), totalHits);
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

    private ScriptScoreQueryBuilder getScriptBuilder(String field, double[] embeddingQuery, float boost) {
        ScriptType scriptType = ScriptType.INLINE;
        String language = "painless";
        Map<String, Object> params = Collections.singletonMap("queryVector", (Object) embeddingQuery);
        String script = String.format("""
                if (doc['%s'].size() == 0) {
                    return 0.0;
                } else {
                    def score = cosineSimilarity(params.queryVector, doc['%s']) + 1.0;
                    if (Double.isNaN(score) || score < 0) {
                        return 0.0;
                    } else {
                        return score;
                    }
                }
                """, field, field);
        return QueryBuilders.scriptScoreQuery(
                QueryBuilders.matchAllQuery(),
                new Script(scriptType, language, script, params)
        ).boost(boost);
    }

    private String normalizeQuery(String query) {
        query = query.replaceAll("[,;!&$?№~@%^*+:<>=]", "")
                .replaceAll("[-._]", " ")
                .replaceAll("/", " ")
                .replaceAll("[\\s]+", " ");
        return query.trim();
    }
}
