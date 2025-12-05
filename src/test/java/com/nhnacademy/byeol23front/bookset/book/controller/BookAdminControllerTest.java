package com.nhnacademy.byeol23front.bookset.book.controller;

import com.nhnacademy.byeol23front.auth.AuthUtil;
import com.nhnacademy.byeol23front.bookset.book.client.BookApiClient;
import com.nhnacademy.byeol23front.bookset.book.dto.BookCreateRequest;
import com.nhnacademy.byeol23front.bookset.book.dto.BookResponse;
import com.nhnacademy.byeol23front.bookset.book.dto.BookStatus;
import com.nhnacademy.byeol23front.bookset.book.dto.BookUpdateRequest;
import com.nhnacademy.byeol23front.bookset.category.client.CategoryApiClient;
import com.nhnacademy.byeol23front.bookset.category.dto.CategoryLeafResponse;
import com.nhnacademy.byeol23front.bookset.category.dto.CategoryListResponse;
import com.nhnacademy.byeol23front.bookset.contributor.client.ContributorApiClient;
import com.nhnacademy.byeol23front.bookset.contributor.dto.AllContributorResponse;
import com.nhnacademy.byeol23front.bookset.publisher.client.PublisherApiClient;
import com.nhnacademy.byeol23front.bookset.publisher.dto.AllPublishersInfoResponse;
import com.nhnacademy.byeol23front.bookset.tag.client.TagApiClient;
import com.nhnacademy.byeol23front.bookset.tag.dto.AllTagsInfoResponse;
import com.nhnacademy.byeol23front.bookset.tag.dto.PageResponse;
import com.nhnacademy.byeol23front.minio.service.MinioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(BookAdminController.class)
class BookAdminControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private BookApiClient bookApiClient;

	@MockitoBean
	private CategoryApiClient categoryApiClient;

	@MockitoBean
	private TagApiClient tagApiClient;

	@MockitoBean
	private ContributorApiClient contributorApiClient;

	@MockitoBean
	private PublisherApiClient publisherApiClient;

	@MockitoBean
	private MinioService minioService;

	@MockitoBean(name = "authHelper")
	private AuthUtil authUtil;

	private AllPublishersInfoResponse publisherInfo;
	private List<CategoryLeafResponse> bookCategories;
	private List<AllTagsInfoResponse> bookTags;
	private List<AllContributorResponse> bookContributors;

	@BeforeEach
	void setUp() {
		publisherInfo = new AllPublishersInfoResponse(7L, "테스트출판사");
		bookCategories = List.of(new CategoryLeafResponse(1L, "국내도서", "국내도서>소설"));
		bookTags = List.of(new AllTagsInfoResponse(2L, "베스트셀러"));
		bookContributors = List.of(new AllContributorResponse(3L, "홍길동", "AUTHOR"));

		// 템플릿에서 principal.nickname 접근을 방지하기 위해 false로 설정
		when(authUtil.isLoggedIn()).thenReturn(false);
		when(authUtil.isAdmin()).thenReturn(true);
	}

	@Test
	@DisplayName("POST /admin/books/new - 필수 데이터로 도서 생성 성공")
	void createBook_success() throws Exception {
		BookResponse createdBook = createBookResponse(10L);
		ArgumentCaptor<BookCreateRequest> captor = ArgumentCaptor.forClass(BookCreateRequest.class);

		when(bookApiClient.createBook(any(BookCreateRequest.class))).thenReturn(createdBook);

		mockMvc.perform(post("/admin/books/new")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.with(csrf())
				.with(user("admin").roles("ADMIN"))
				.param("bookName", "테스트 도서")
				.param("toc", "목차")
				.param("description", "설명")
				.param("regularPrice", "20000")
				.param("salePrice", "15000")
				.param("isbn", "9781234567890")
				.param("publishDate", "2024-01-10")
				.param("isPack", "true")
				.param("bookStatus", "SALE")
				.param("stock", "30")
				.param("publisherId", "7")
				.param("categoryIds", "1")
				.param("tagIds", "2")
				.param("contributorIds", "3"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/admin/books"));

		verify(bookApiClient).createBook(captor.capture());
		verifyNoInteractions(minioService);

		BookCreateRequest sentRequest = captor.getValue();
		assertThat(sentRequest.bookName()).isEqualTo("테스트 도서");
		assertThat(sentRequest.isPack()).isTrue();
		assertThat(sentRequest.categoryIds()).containsExactly(1L);
		assertThat(sentRequest.tagIds()).containsExactly(2L);
		assertThat(sentRequest.contributorIds()).containsExactly(3L);
	}

	@Test
	@DisplayName("POST /admin/books/new - 카테고리 미선택 시 다시 작성 페이지로 리다이렉트")
	void createBook_withoutCategoryIds_redirects() throws Exception {
		mockMvc.perform(post("/admin/books/new")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.with(csrf())
				.with(user("admin").roles("ADMIN"))
				.param("bookName", "테스트 도서"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/admin/books/new"));

		verify(bookApiClient, never()).createBook(any());
		verifyNoInteractions(minioService);
	}

	@Test
	@DisplayName("POST /admin/books/{book-id} - 도서 정보 수정 성공")
	@SuppressWarnings("unchecked")
	void updateBook_success() throws Exception {
		long bookId = 15L;
		ArgumentCaptor<BookUpdateRequest> captor = ArgumentCaptor.forClass(BookUpdateRequest.class);

		when(bookApiClient.updateBook(eq(bookId), any(BookUpdateRequest.class))).thenReturn(createBookResponse(bookId));

		mockMvc.perform(post("/admin/books/{book-id}", bookId)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.with(csrf())
				.with(user("admin").roles("ADMIN"))
				.param("bookName", "수정 도서")
				.param("toc", "새 목차")
				.param("description", "새 설명")
				.param("regularPrice", "18000")
				.param("salePrice", "12000")
				.param("publishDate", "2024-03-10")
				.param("isPack", "false")
				.param("bookStatus", "SALE")
				.param("publisherId", "7")
				.param("categoryIds", "1")
				.param("tagIds", "2")
				.param("contributorIds", "3"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/admin/books"));

		verify(bookApiClient).updateBook(eq(bookId), captor.capture());
		verifyNoInteractions(minioService);

		BookUpdateRequest sentRequest = captor.getValue();
		assertThat(sentRequest.bookName()).isEqualTo("수정 도서");
		assertThat(sentRequest.isPack()).isFalse();
		assertThat(sentRequest.categoryIds()).containsExactly(1L);
		assertThat(sentRequest.tagIds()).containsExactly(2L);
	}

	@Test
	@DisplayName("POST /admin/books/{book-id} - 카테고리 미선택 시 수정 화면으로 리다이렉트")
	void updateBook_withoutCategoryIds_redirects() throws Exception {
		long bookId = 20L;

		mockMvc.perform(post("/admin/books/{book-id}", bookId)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.with(csrf())
				.with(user("admin").roles("ADMIN"))
				.param("bookName", "수정 도서"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/admin/books/update/" + bookId));

		verify(bookApiClient, never()).updateBook(eq(bookId), any());
		verifyNoInteractions(minioService);
	}

	@Test
	@DisplayName("GET /admin/books/update/{book-id} - 수정 폼 렌더링 데이터 확인")
	void bookUpdateForm_populatesModel() throws Exception {
		long bookId = 30L;
		BookResponse bookResponse = createBookResponse(bookId);

		when(bookApiClient.getBook(bookId)).thenReturn(ResponseEntity.ok(bookResponse));
		when(categoryApiClient.getRoots()).thenReturn(List.of(new CategoryListResponse(10L, "루트", false)));
		when(tagApiClient.getAllTags(0, 100)).thenReturn(ResponseEntity.ok(new PageResponse<>(bookTags, 0, 100, 1, 1, true, true)));
		when(contributorApiClient.getAllContributors(0, 1000))
			.thenReturn(ResponseEntity.ok(new com.nhnacademy.byeol23front.bookset.contributor.dto.PageResponse<>(bookContributors, 0, 1000, true, true, 1, 1)));
		when(publisherApiClient.getAllPublishers(0, 100))
			.thenReturn(ResponseEntity.ok(new com.nhnacademy.byeol23front.bookset.publisher.dto.PageResponse<>(List.of(publisherInfo), 0, 100, 1, 1, true, true)));

		mockMvc.perform(get("/admin/books/update/{book-id}", bookId)
				.with(user("admin").roles("ADMIN")))
			.andExpect(status().isOk())
			.andExpect(view().name("admin/book/bookUpdateForm"))
			.andExpect(model().attribute("book", bookResponse))
			.andExpect(model().attributeExists("categories", "allTags", "allContributors", "allPublishers"))
			.andExpect(model().attribute("selectedCategoryIds", List.of(1L)))
			.andExpect(model().attribute("selectedTagIds", List.of(2L)))
			.andExpect(model().attribute("selectedContributorIds", List.of(3L)));
	}

	@Test
	@DisplayName("GET /admin/books - 목록 조회 응답이 없을 때 빈 모델로 초기화")
	void getBooks_handlesEmptyBody() throws Exception {
		int page = 0;
		int size = 20;
		ResponseEntity<PageResponse<BookResponse>> emptyResponse = ResponseEntity.ok(null);

		when(bookApiClient.getBooks(page, size)).thenReturn(emptyResponse);

		PageResponse<BookResponse> expected = new PageResponse<>(List.of(), page, size, 0, 0, true, true);

		mockMvc.perform(get("/admin/books")
				.with(user("admin").roles("ADMIN"))
				.param("page", String.valueOf(page))
				.param("size", String.valueOf(size)))
			.andExpect(status().isOk())
			.andExpect(view().name("admin/book/bookList"))
			.andExpect(model().attribute("books", List.of()))
			.andExpect(model().attribute("paging", expected));

		verify(bookApiClient).getBooks(page, size);
	}

	private BookResponse createBookResponse(Long bookId) {
		return new BookResponse(
			bookId,
			"테스트 도서",
			"목차",
			"설명",
			new BigDecimal("20000"),
			new BigDecimal("15000"),
			"9781234567890",
			LocalDate.of(2024, 1, 10),
			true,
			BookStatus.SALE,
			30,
			publisherInfo,
			false,
			bookCategories,
			bookTags,
			bookContributors,
			List.of()
		);
	}

}

