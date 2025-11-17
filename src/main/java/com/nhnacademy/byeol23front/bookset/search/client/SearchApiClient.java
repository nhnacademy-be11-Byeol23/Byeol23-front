package com.nhnacademy.byeol23front.bookset.search.client;

import com.nhnacademy.byeol23front.bookset.search.dto.BookSearchResultResponse;
import com.nhnacademy.byeol23front.bookset.search.dto.SearchCondition;
import com.nhnacademy.byeol23front.bookset.search.dto.SearchPageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(
        name = "BYEOL23-GATEWAY",
        contextId = "searchApiClient"
)
public interface SearchApiClient {
    @GetMapping("/search-api/search")
    List<BookSearchResultResponse> searchBooksByQuery(@SpringQueryMap SearchCondition condition);

    @GetMapping("/search-api/categories/{category-id}/books")
    SearchPageResponse<BookSearchResultResponse> searchBooksByCategory(@PathVariable("category-id") Long id, @SpringQueryMap SearchCondition condition);
}
