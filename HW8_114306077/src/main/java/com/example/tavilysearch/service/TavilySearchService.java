package com.example.tavilysearch.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class TavilySearchService {

    private static final Logger logger = LoggerFactory.getLogger(TavilySearchService.class);
    private static final String SEARCH_URL = "https://api.tavily.com/search";

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${tavily.enabled:false}")
    private boolean enabled;

    @Value("${tavily.api-key:}")
    private String apiKey;

    public List<SearchResult> search(String query) {
        if (!enabled || apiKey == null || apiKey.isBlank()) {
            throw new SearchException(
                    "搜尋服務尚未設定。請設定 TAVILY_ENABLED=true 與 TAVILY_API_KEY 後重新啟動。");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        SearchRequest body = new SearchRequest(apiKey, query, 10);
        HttpEntity<SearchRequest> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<TavilyResponse> response = restTemplate.exchange(
                    SEARCH_URL,
                    HttpMethod.POST,
                    request,
                    new ParameterizedTypeReference<>() {
                    });
            TavilyResponse responseBody = response.getBody();
            return responseBody == null || responseBody.results() == null
                    ? List.of()
                    : responseBody.results().stream()
                            .filter(result -> result != null)
                            .filter(result -> result.title() != null && !result.title().isBlank())
                            .filter(result -> result.url() != null && !result.url().isBlank())
                            .toList();
        } catch (RestClientException exception) {
            logger.error("Tavily search request failed", exception);
            throw new SearchException("目前無法連線到搜尋服務，請稍後再試。", exception);
        }
    }

    private record SearchRequest(String api_key, String query, int max_results) {
    }

    private record TavilyResponse(List<SearchResult> results) {
    }

    public record SearchResult(String title, String url, String content) {
    }
}
