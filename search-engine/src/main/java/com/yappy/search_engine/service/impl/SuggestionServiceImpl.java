package com.yappy.search_engine.service.impl;

import com.yappy.search_engine.helper.Indices;
import com.yappy.search_engine.service.SuggestionService;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SuggestionServiceImpl implements SuggestionService {

    private final RestHighLevelClient client;

    @Autowired
    public SuggestionServiceImpl(RestHighLevelClient client) {
        this.client = client;
    }

    @Override
    public List<String> getAutocomplete(String query, int page, int size) {
        SearchRequest searchRequest = new SearchRequest(Indices.SUGGESTIONS_INDEX);
        final int from = page <= 0 ? 0 : page * size;
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .from(from)
                .size(size);

        MatchQueryBuilder matchQuery = QueryBuilders.matchQuery("suggestion", query)
                .fuzziness("AUTO");
        searchSourceBuilder.query(matchQuery);
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse;
        try {
            searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return extractSuggestionsFromResponse(searchResponse);
    }

    private List<String> extractSuggestionsFromResponse(SearchResponse searchResponse) {
        List<String> suggestion = new ArrayList<>();
        for (org.elasticsearch.search.SearchHit hit : searchResponse.getHits().getHits()) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            suggestion.add(sourceAsMap.getOrDefault("suggestion", "").toString());
        }
        return suggestion;
    }
}
