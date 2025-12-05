package com.nhnacademy.byeol23front.bookset.search.controller;

import com.nhnacademy.byeol23front.auth.AuthUtil;
import com.nhnacademy.byeol23front.bookset.category.client.CategoryApiClient;
import com.nhnacademy.byeol23front.bookset.category.dto.CategoryTreeResponse;
import com.nhnacademy.byeol23front.bookset.search.client.SearchApiClient;
import com.nhnacademy.byeol23front.bookset.search.dto.BookSearchResultResponse;
import com.nhnacademy.byeol23front.bookset.search.dto.SearchPageResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookSearchController.class)
@Import(AuthUtil.class)
@AutoConfigureMockMvc(addFilters = false)
class BookSearchControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockitoBean
    private AuthUtil authUtil;
    @MockitoBean
    SearchApiClient searchApiClient;
    @MockitoBean
    CategoryApiClient categoryApiClient;

    private BookSearchResultResponse sampleBook() {
        return new BookSearchResultResponse(
                "1",
                "http://image.jpg",
                "Sample Book",
                List.of("Author"),
                "Publisher",
                LocalDate.of(2024, 1, 1),
                20000,
                15000,
                10,
                4.5f,
                false
        );
    }

    private List<CategoryTreeResponse> sampleCategoryTree() {
        return List.of(
                new CategoryTreeResponse(
                        1L,
                        "Root Category",
                        "1",
                        List.of()
                )
        );
    }

    private SearchPageResponse<BookSearchResultResponse> samplePage() {
        return new SearchPageResponse<>(
                0,
                10,
                1L,
                1,
                List.of(sampleBook())
        );
    }

    @Test
    @DisplayName("검색 조회 /search 테스트")
    void searchBooksByQuery() throws Exception {
        Mockito.when(searchApiClient.searchBooksByQuery(any()))
                .thenReturn(List.of(sampleBook()));

        Mockito.when(categoryApiClient.getRootsWithChildren2Depth())
                .thenReturn(sampleCategoryTree());

        mockMvc.perform(get("/search")
                        .param("query", "spring"))
                .andExpect(status().isOk())
                .andExpect(view().name("book/search/semantic-search"))
                .andExpect(model().attributeExists("roots"))
                .andExpect(model().attributeExists("results"))
                .andExpect(model().attribute("query", "spring"));
    }

    @Test
    @DisplayName("카테고리 검색 /categories/{id}/books 테스트")
    void searchBooksByCategory() throws Exception {
        Mockito.when(searchApiClient.searchBooksByCategory(eq(10L), any(), eq(0), eq(10)))
                .thenReturn(samplePage());

        Mockito.when(categoryApiClient.getRootsWithChildren2Depth())
                .thenReturn(sampleCategoryTree());

        mockMvc.perform(get("/categories/10/books")
                        .param("pathId", "100")
                        .param("sort", "POPULAR")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(view().name("book/search/category-search"))
                .andExpect(model().attributeExists("roots"))
                .andExpect(model().attributeExists("results"))
                .andExpect(model().attribute("categoryId", 10L))
                .andExpect(model().attribute("pathId", "100"))
                .andExpect(model().attribute("sort", "POPULAR"));
    }

    @Test
    @DisplayName("베스트 도서 /best 테스트")
    void searchBestBooks() throws Exception {
        Mockito.when(searchApiClient.searchBestBooks(0, 10))
                .thenReturn(samplePage());
        Mockito.when(categoryApiClient.getRootsWithChildren2Depth())
                .thenReturn(sampleCategoryTree());

        mockMvc.perform(get("/best")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(view().name("book/search/best"))
                .andExpect(model().attributeExists("roots"))
                .andExpect(model().attributeExists("results"));
    }

    @Test
    @DisplayName("신간 도서 /new 테스트")
    void searchNewBooks() throws Exception {
        Mockito.when(searchApiClient.searchNewBooks(any(), eq(0), eq(10)))
                .thenReturn(samplePage());

        Mockito.when(categoryApiClient.getRootsWithChildren2Depth())
                .thenReturn(sampleCategoryTree());

        mockMvc.perform(get("/new")
                        .param("sort", "NEWEST")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(view().name("book/search/new"))
                .andExpect(model().attributeExists("roots"))
                .andExpect(model().attributeExists("results"))
                .andExpect(model().attribute("sort", "NEWEST"));
    }
}
