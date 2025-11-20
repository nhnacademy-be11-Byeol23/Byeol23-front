package com.nhnacademy.byeol23front.bookset.search.controller;

import com.nhnacademy.byeol23front.bookset.category.client.CategoryApiClient;
import com.nhnacademy.byeol23front.bookset.search.client.SearchApiClient;
import com.nhnacademy.byeol23front.bookset.search.dto.SearchCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;

@Controller
@RequiredArgsConstructor
public class BookSearchController {
    private final SearchApiClient searchApiClient;
    private final CategoryApiClient categoryApiClient;

    @GetMapping("/search")
    public String searchBooksByQuery(SearchCondition condition, Model model) throws InterruptedException, ExecutionException {
        try(var taskScope = new StructuredTaskScope.ShutdownOnFailure()) {
            var searchResult = taskScope.fork(() -> searchApiClient.searchBooksByQuery(condition));
            var backResult = taskScope.fork(categoryApiClient::getRootsWithChildren2Depth);

            taskScope.join();
            taskScope.throwIfFailed();

            model.addAttribute("query", condition.getQuery());
            model.addAttribute("roots", backResult.get());
            model.addAttribute("results", searchResult.get());

            return "book/search/semantic-search";
        }
    }

    @GetMapping("/categories/{category-id}/books")
    public String searchBooksByCategory(@PathVariable("category-id") Long id, SearchCondition condition, Pageable pageable, Model model) throws InterruptedException, ExecutionException{
        try(var taskScope = new StructuredTaskScope.ShutdownOnFailure()) {
            var searchResult = taskScope.fork(() -> searchApiClient.searchBooksByCategory(id, condition, pageable.getPageNumber(), pageable.getPageSize()));
            var backResult = taskScope.fork(categoryApiClient::getRootsWithChildren2Depth);

            taskScope.join();
            taskScope.throwIfFailed();

            model.addAttribute("roots", backResult.get());
            model.addAttribute("results", searchResult.get());
            model.addAttribute("categoryId", id);
            model.addAttribute("pathId", condition.getPathId());
            model.addAttribute("sort", condition.getSort() == null ? "POPULAR" : condition.getSort().name());
            return "book/search/category-search";
        }
    }

    @GetMapping("/best")
    public String searchBestBooks(Pageable pageable, Model model) throws InterruptedException, ExecutionException{
        try(var taskScope = new StructuredTaskScope.ShutdownOnFailure()) {
            var searchResult = taskScope.fork(() -> searchApiClient.searchBestBooks(pageable.getPageNumber(), pageable.getPageSize()));
            var backResult = taskScope.fork(categoryApiClient::getRootsWithChildren2Depth);

            taskScope.join();
            taskScope.throwIfFailed();

            model.addAttribute("roots", backResult.get());
            model.addAttribute("results", searchResult.get());

            return "book/search/best";
        }
    }

    @GetMapping("/new")
    public String searchNewBooks(SearchCondition condition, @PageableDefault(sort = {}) Pageable pageable, Model model) throws InterruptedException, ExecutionException{
        try(var taskScope = new StructuredTaskScope.ShutdownOnFailure()) {
            var searchResult = taskScope.fork(() -> searchApiClient.searchNewBooks(condition, pageable.getPageNumber(), pageable.getPageSize()));
            var backResult = taskScope.fork(categoryApiClient::getRootsWithChildren2Depth);

            taskScope.join();
            taskScope.throwIfFailed();

            model.addAttribute("roots", backResult.get());
            model.addAttribute("results", searchResult.get());
            model.addAttribute("sort", condition.getSort() == null ? "NEWEST" : condition.getSort().name());
            return "book/search/new";
        }
    }
}
