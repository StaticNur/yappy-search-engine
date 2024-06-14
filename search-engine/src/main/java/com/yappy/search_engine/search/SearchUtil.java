package com.yappy.search_engine.search;

import com.yappy.search_engine.dto.SearchRequestDto;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.util.CollectionUtils;

import java.util.List;

public final class SearchUtil {

    private SearchUtil() {
    }

    public static SearchRequest buildSearchRequest(final String indexName,
                                                   final String field,
                                                   final String date) {
        try {
            final SearchSourceBuilder builder = new SearchSourceBuilder()
                    .postFilter(getQueryBuilder(field, date));

            final SearchRequest request = new SearchRequest(indexName);
            request.source(builder);

            return request;
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static SearchRequest buildSearchRequest(final String indexName,
                                                   final SearchRequestDto dto,
                                                   final String date) {
        try {
            final int page = dto.getPage();
            final int size = dto.getSize();
            final int from = page <= 0 ? 0 : page * size;
            final QueryBuilder dateQuery = getQueryBuilder("created", date);
            SearchSourceBuilder builder = new SearchSourceBuilder()
                    .from(from)
                    .size(size)
                    .postFilter(dateQuery);

            if (dto.getSortBy() != null) {
                builder = builder.sort(
                        dto.getSortBy(),
                        dto.getOrder() != null ? dto.getOrder() : SortOrder.DESC //DESC от большего к меньшему
                );
            }

            BoolQueryBuilder boolQueryBuilder = getQueryBuilder(dto.getQuery());
            builder.query(boolQueryBuilder);

            final SearchRequest request = new SearchRequest(indexName);
            request.source(builder);

            return request;
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static BoolQueryBuilder getQueryBuilder(String query) {
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
        return boolQueryBuilder;
    }

    /*private static QueryBuilder getQueryBuilder(final SearchRequestDto dto) {
        if (dto == null) {
            return null;
        }

        return null;
    }*/

    private static QueryBuilder getQueryBuilder(final String field, final String date) {
        return QueryBuilders.rangeQuery(field).gte(date);
    }
}