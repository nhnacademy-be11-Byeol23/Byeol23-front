package com.nhnacademy.byeol23front.bookset.search.controller;

import com.nhnacademy.byeol23front.bookset.category.client.CategoryApiClient;
import com.nhnacademy.byeol23front.bookset.category.dto.CategoryListResponse;
import com.nhnacademy.byeol23front.bookset.search.client.SearchApiClient;
import com.nhnacademy.byeol23front.bookset.search.dto.BookSearchResultResponse;
import com.nhnacademy.byeol23front.bookset.search.dto.SearchCondition;
import com.nhnacademy.byeol23front.bookset.search.dto.SearchPageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
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
            var backResult = taskScope.fork(() -> categoryApiClient.getRootsWithChildren2Depth());

            taskScope.join();
            taskScope.throwIfFailed();

            model.addAttribute("query", condition.getQuery());
            model.addAttribute("roots", backResult.get());
            model.addAttribute("results", searchResult.get());

            return "book/search/semantic-search";
        }
    }

    @GetMapping("/categories/{category-id}/books")
    public String searchBooksByCategory(@PathVariable("category-id") Long id, SearchCondition condition, Model model) {
        SearchPageResponse<BookSearchResultResponse> result = searchApiClient.searchBooksByCategory(id, condition);
        model.addAttribute("result", result);
        return "book/search/category-search";
    }
}
