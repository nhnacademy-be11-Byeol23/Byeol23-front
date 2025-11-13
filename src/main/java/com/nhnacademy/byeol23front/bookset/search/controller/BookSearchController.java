package com.nhnacademy.byeol23front.bookset.search.controller;

import com.nhnacademy.byeol23front.bookset.search.client.SearchApiClient;
import com.nhnacademy.byeol23front.bookset.search.dto.BookSearchResultResponse;
import com.nhnacademy.byeol23front.bookset.search.dto.SearchCondition;
import com.nhnacademy.byeol23front.bookset.search.dto.SearchPageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BookSearchController {
    private final SearchApiClient searchApiClient;

    @GetMapping("/categories/{category-id}/books")
    public SearchPageResponse<BookSearchResultResponse> searchBooksByCategory(@PathVariable("category-id") Long id, SearchCondition condition) {
        return searchApiClient.searchBooksByCategory(id, condition);
    }
}
