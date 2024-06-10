package com.yappy.search_engine.service;

import java.util.List;

public interface SuggestionService {
    List<String> getAutocomplete(String query, int page, int size);
}
