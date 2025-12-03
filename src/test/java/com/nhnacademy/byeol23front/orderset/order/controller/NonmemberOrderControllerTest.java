package com.nhnacademy.byeol23front.orderset.order.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.nhnacademy.byeol23front.auth.AuthHelper;
import com.nhnacademy.byeol23front.bookset.book.dto.BookStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.Model;

import com.nhnacademy.byeol23front.bookset.book.client.BookApiClient;
import com.nhnacademy.byeol23front.bookset.book.dto.BookInfoRequest;
import com.nhnacademy.byeol23front.bookset.book.dto.BookResponse;
import com.nhnacademy.byeol23front.bookset.category.client.CategoryApiClient;
import com.nhnacademy.byeol23front.bookset.contributor.dto.AllContributorResponse;
import com.nhnacademy.byeol23front.bookset.publisher.dto.AllPublishersInfoResponse;
import com.nhnacademy.byeol23front.minio.dto.back.GetUrlResponse;
import com.nhnacademy.byeol23front.orderset.order.client.OrderApiClient;
import com.nhnacademy.byeol23front.orderset.packaging.client.PackagingApiClient;

@WebMvcTest(NonmemberOrderController.class)
@Import(AuthHelper.class)
class NonmemberOrderControllerTest {

	@Autowired
	private MockMvc mockMvc;

	// --- Controller Dependencies (실제 의존성만 Mocking) ---
	@MockBean
	private BookApiClient bookApiClient;
	@MockBean
	private OrderApiClient orderApiClient;
	@MockBean
	private PackagingApiClient packagingApiClient;
	@MockBean
	private OrderUtil orderUtil;

	@MockBean
	private AuthHelper authHelper;

	@MockBean
	private CategoryApiClient categoryApiClient;

	// --- Test Data ---
	private BookResponse mockBookResponse;
	private Long bookId = 1L;
	private int quantity = 2;
	private String defaultImageUrl = "https://image.yes24.com/momo/Noimg_L.jpg";
	private String realImageUrl = "http://image.url/book1.png";

	@BeforeEach
	void setUp() {
		// --- [제거] Filter Mocks ---
		// Claims mockClaims = Mockito.mock(Claims.class);
		// given(jwtParser.parseToken(anyString())).willReturn(mockClaims);
		// ... (MemberRepository 관련 given 제거) ...

		// 2. Test Data Initialization
		List<GetUrlResponse> images = List.of(new GetUrlResponse(1L, realImageUrl));
		AllPublishersInfoResponse publisher = new AllPublishersInfoResponse(1L, "Test Publisher");
		List<AllContributorResponse> contributors = List.of(new AllContributorResponse(1L, "Test Author", "AUTHOR"));

		mockBookResponse = new BookResponse(
			bookId, "Test Book", "TOC", "Desc",
			new BigDecimal("20000"), new BigDecimal("18000"),
			"1234567890123", LocalDate.now(), true, BookStatus.SALE, 10,
			publisher, false, null, null, contributors, images
		);
	}

	@Test
	@DisplayName("GET /orders/nonmember/direct - 비회원 바로 구매 페이지 로드 성공")
	void getOrderFormDirect_Success() throws Exception {
		// given
		// 1. Mock BookApiClient
		given(bookApiClient.getBook(bookId)).willReturn(ResponseEntity.ok(mockBookResponse));

		// 2. Mock OrderUtil (모든 void 메서드의 동작을 정의)
		doNothing().when(orderUtil).addTotalQuantity(any(Model.class), any(List.class));
		doNothing().when(orderUtil).addDeliveryDatesToModel(any(Model.class));
		doNothing().when(orderUtil).addOrderSummary(any(Model.class), any(List.class));
		doNothing().when(orderUtil).addDeliveryFeeToModel(any(Model.class), any());
		doNothing().when(orderUtil).addPackagingOption(any(Model.class));

		// when & then
		mockMvc.perform(get("/orders/nonmember/direct")
				.param("bookId", String.valueOf(bookId))
				.param("quantity", String.valueOf(quantity))
				// [추가] /admin이 아니므로 CSRF는 필요 없으나,
				// Security가 활성화되어 있다면 '익명 사용자'로 처리
				.with(user("anonymousUser").roles("ANONYMOUS"))
			)
			.andExpect(status().isOk())
			.andExpect(view().name("order/nonmemberCheckout"))
			.andExpect(model().attributeExists("orderItem", "quantity"))
			.andExpect(model().attribute("quantity", quantity));

		// Verify (모든 의존성이 1번씩 호출되었는지 검증)
		verify(bookApiClient, times(1)).getBook(bookId);
		verify(orderUtil, times(1)).addTotalQuantity(any(Model.class), any(List.class));
		verify(orderUtil, times(1)).addDeliveryDatesToModel(any(Model.class));
		verify(orderUtil, times(1)).addOrderSummary(any(Model.class), any(List.class));
		verify(orderUtil, times(1)).addDeliveryFeeToModel(any(Model.class), any());
		verify(orderUtil, times(1)).addPackagingOption(any(Model.class));
	}

	@Test
	@DisplayName("GET /orders/nonmember/direct - 이미지가 없을 때 기본 이미지 사용 검증")
	void getOrderFormDirect_NoImages_UsesDefaultImageUrl() throws Exception {
		// given
		// 1. 이미지가 없는(null) BookResponse 생성
		BookResponse bookWithNoImages = new BookResponse(
			bookId, "No Image Book", "TOC", "Desc",
			new BigDecimal("20000"), new BigDecimal("18000"),
			"1234567890123", LocalDate.now(), true, BookStatus.SALE, 10,
			new AllPublishersInfoResponse(1L, "Test Publisher"),
			false, null, null,
			new ArrayList<>(), // 빈 리스트
			null // 또는 null
		);

		given(bookApiClient.getBook(bookId)).willReturn(ResponseEntity.ok(bookWithNoImages));

		// 2. OrderUtil.addOrderSummary 메서드로 전달되는 List<BookInfoRequest>를 캡처할 준비
		ArgumentCaptor<List<BookInfoRequest>> captor = ArgumentCaptor.forClass(List.class);
		doNothing().when(orderUtil).addOrderSummary(any(Model.class), captor.capture());

		// when
		mockMvc.perform(get("/orders/nonmember/direct")
				.param("bookId", String.valueOf(bookId))
				.param("quantity", String.valueOf(quantity))
				.with(user("anonymousUser").roles("ANONYMOUS"))
			)
			.andExpect(status().isOk())
			.andExpect(view().name("order/nonmemberCheckout"));

		// then
		// 3. 캡처된 List<BookInfoRequest>를 가져와서 내부의 imageUrl 검증
		List<BookInfoRequest> capturedList = captor.getValue();

		assertThat(capturedList).isNotNull();
		assertThat(capturedList).hasSize(1);
		// 4. private 메서드인 getBookOrderRequest가 기본 이미지 URL을 올바르게 설정했는지 확인
		assertThat(capturedList.get(0).imageUrl()).isEqualTo(defaultImageUrl);
	}

	@Test
	@DisplayName("GET /orders/nonmember/direct - bookId 누락 시 400 Bad Request")
	void getOrderFormDirect_MissingBookId_ShouldReturnBadRequest() throws Exception {
		// when & then
		// @RequestParam(name = "bookId")는 'required=true'가 기본값이므로 400 에러 발생
		mockMvc.perform(get("/orders/nonmember/direct")
				.param("quantity", String.valueOf(quantity))
				.with(user("anonymousUser").roles("ANONYMOUS"))
			)
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("GET /orders/nonmember/direct - quantity 누락 시 400 Bad Request")
	void getOrderFormDirect_MissingQuantity_ShouldReturnBadRequest() throws Exception {
		// when & then
		mockMvc.perform(get("/orders/nonmember/direct")
				.param("bookId", String.valueOf(bookId))
				.with(user("anonymousUser").roles("ANONYMOUS"))
			)
			.andExpect(status().isBadRequest());
	}
}