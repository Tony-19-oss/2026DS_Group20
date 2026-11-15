package com.example.tavilysearch.controller;

import com.example.tavilysearch.service.SearchException;
import com.example.tavilysearch.service.TavilySearchService;
import com.example.tavilysearch.service.TavilySearchService.SearchResult;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SearchController {

    private final TavilySearchService searchService;

    public SearchController(TavilySearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/")
    public String search(
            @RequestParam(name = "q", required = false) String query,
            Model model) {
        String normalizedQuery = query == null ? "" : query.trim();
        model.addAttribute("query", normalizedQuery);
        model.addAttribute("results", List.of());
        model.addAttribute("searched", false);

        if (!normalizedQuery.isEmpty()) {
            try {
                List<SearchResult> results = searchService.search(normalizedQuery);
                model.addAttribute("results", results);
                model.addAttribute("searched", true);
            } catch (SearchException exception) {
                model.addAttribute("error", exception.getMessage());
            }
        }

        return "index";
    }
}
