// package com.nhnacademy.byeol23front.orderset.order.controller;
//
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.nhnacademy.byeol23front.bookset.book.dto.BookOrderInfoResponse;
// import com.nhnacademy.byeol23front.bookset.category.client.CategoryApiClient;
// import com.nhnacademy.byeol23front.orderset.order.client.OrderApiClient;
// import com.nhnacademy.byeol23front.orderset.order.dto.OrderBulkUpdateRequest;
// import com.nhnacademy.byeol23front.orderset.order.dto.OrderCancelRequest;
// import com.nhnacademy.byeol23front.orderset.order.dto.OrderDetailResponse;
// import com.nhnacademy.byeol23front.orderset.order.dto.OrderInfoResponse;
// import com.nhnacademy.byeol23front.orderset.order.dto.OrderSearchCondition;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.mockito.ArgumentCaptor;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.PageImpl;
// import org.springframework.data.domain.PageRequest;
// import org.springframework.data.domain.Pageable;
// import org.springframework.http.MediaType;
// import org.springframework.http.ResponseEntity;
// import org.springframework.test.web.servlet.MockMvc;
//
// import java.math.BigDecimal;
// import java.time.LocalDateTime;
// import java.util.List;
//
// import static org.assertj.core.api.Assertions.assertThat;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.eq;
// import static org.mockito.BDDMockito.given;
// import static org.mockito.Mockito.times;
// import static org.mockito.Mockito.verify;
// // Security Test 의존성 (CSRF 및 인증)
// import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
// import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
// @WebMvcTest(OrderAdminController.class)
// class OrderAdminControllerTest {
//
// 	@Autowired
// 	private MockMvc mockMvc;
//
// 	@Autowired
// 	private ObjectMapper objectMapper;
//
// 	@MockBean
// 	private OrderApiClient orderApiClient;
//
// 	// (Context Load를 위한 @ControllerAdvice 의존성 Mocking)
// 	@MockBean
// 	private CategoryApiClient categoryApiClient;
//
// 	// --- 테스트용 공통 데이터 ---
// 	private String testOrderNumber;
// 	private OrderDetailResponse testOrderDetailResponse;
// 	private OrderInfoResponse testOrderInfoResponse;
// 	private Pageable defaultPageable;
//
// 	@BeforeEach
// 	void setUp() {
// 		testOrderNumber = "20251115-TEST123";
//
// 		// GET /{order-number} 응답 DTO
// 		testOrderDetailResponse = new OrderDetailResponse(
// 			testOrderNumber, LocalDateTime.now(), "ORDERED", new BigDecimal("15000"),
// 			"홍길동", "01012345678", "주소", "상세주소", "12345",
// 			List.of(new BookOrderInfoResponse("테스트 책", 1, new BigDecimal("15000")))
// 		);
//
// 		// GET / 응답 DTO
// 		testOrderInfoResponse = new OrderInfoResponse(
// 			testOrderNumber, LocalDateTime.now(), "홍길동", new BigDecimal("15000"), "ORDERED"
// 		);
//
// 		defaultPageable = PageRequest.of(0, 10);
// 	}
//
// 	@Test
// 	@DisplayName("POST /admin/orders/{orderNumber}/cancel - 주문 취소 성공")
// 	void cancelOrder_Success() throws Exception {
// 		// given
// 		OrderCancelRequest cancelRequest = new OrderCancelRequest("ADMIN_CANCEL");
// 		// Feign Client는 void를 반환하지 않고 ResponseEntity<Void>를 반환한다고 가정
// 		given(orderApiClient.cancelOrder(eq(testOrderNumber), any(OrderCancelRequest.class)))
// 			.willReturn(ResponseEntity.ok().build());
//
// 		// when & then
// 		mockMvc.perform(post("/admin/orders/{orderNumber}/cancel", testOrderNumber)
// 				.with(csrf()) // CSRF 토큰 추가
// 				.with(user("admin").roles("ADMIN")) // ADMIN 권한으로 인증
// 				.contentType(MediaType.APPLICATION_JSON)
// 				.content(objectMapper.writeValueAsString(cancelRequest)))
// 			.andExpect(status().is3xxRedirection()) // 302 Redirect
// 			.andExpect(redirectedUrl("/admin/orders"));
//
// 		verify(orderApiClient, times(1)).cancelOrder(eq(testOrderNumber), any(OrderCancelRequest.class));
// 	}
//
// 	@Test
// 	@DisplayName("GET /admin/orders/{order-number} - 주문 상세 조회 성공")
// 	void getOrderByOrderNumber_Success() throws Exception {
// 		// given
// 		given(orderApiClient.getOrderByOrderNumber(testOrderNumber))
// 			.willReturn(ResponseEntity.ok(testOrderDetailResponse));
//
// 		// when & then
// 		mockMvc.perform(get("/admin/orders/{order-number}", testOrderNumber)
// 				.with(user("admin").roles("ADMIN"))) // ADMIN 권한으로 인증
// 			.andExpect(status().isOk())
// 			.andExpect(view().name("admin/order/order-detail"))
// 			.andExpect(model().attributeExists("orderDetail"))
// 			.andExpect(model().attribute("orderDetail", testOrderDetailResponse));
//
// 		verify(orderApiClient, times(1)).getOrderByOrderNumber(testOrderNumber);
// 	}
//
// 	@Test
// 	@DisplayName("GET /admin/orders - 주문 목록 검색 성공")
// 	void getOrderMain_Success() throws Exception {
// 		// given
// 		Page<OrderInfoResponse> mockPage = new PageImpl<>(List.of(testOrderInfoResponse), defaultPageable, 1);
// 		ResponseEntity<Page<OrderInfoResponse>> mockResponse = ResponseEntity.ok(mockPage);
//
// 		// ArgumentCaptor: @RequestParam이 @ModelAttribute DTO로 잘 바인딩되었는지 검증
// 		ArgumentCaptor<OrderSearchCondition> conditionCaptor = ArgumentCaptor.forClass(OrderSearchCondition.class);
// 		ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
//
// 		given(orderApiClient.searchOrders(conditionCaptor.capture(), pageableCaptor.capture()))
// 			.willReturn(mockResponse);
//
// 		// when & then
// 		mockMvc.perform(get("/admin/orders")
// 				.with(user("admin").roles("ADMIN")) // ADMIN 권한으로 인증
// 				.param("page", "0")
// 				.param("size", "10")
// 				.param("status", "ORDERED")
// 				.param("orderNumber", "TEST")
// 				.param("receiver", "홍길동"))
// 			.andExpect(status().isOk())
// 			.andExpect(view().name("admin/order/order"))
// 			.andExpect(model().attributeExists("orders"))
// 			.andExpect(model().attribute("orders", mockPage));
//
// 		// verify
// 		verify(orderApiClient, times(1)).searchOrders(any(OrderSearchCondition.class), any(Pageable.class));
//
// 		// 캡처된 값 검증
// 		assertThat(conditionCaptor.getValue().getStatus()).isEqualTo("ORDERED");
// 		assertThat(conditionCaptor.getValue().getOrderNumber()).isEqualTo("TEST");
// 		assertThat(conditionCaptor.getValue().getReceiver()).isEqualTo("홍길동");
// 		assertThat(pageableCaptor.getValue().getPageNumber()).isEqualTo(0);
// 	}
//
// 	@Test
// 	@DisplayName("POST /admin/orders/bulk-status - 주문 상태 일괄 변경 성공")
// 	void updateBulkOrderStatus_Success() throws Exception {
// 		// given
// 		OrderBulkUpdateRequest request = new OrderBulkUpdateRequest(List.of("order1", "order2"), "SHIPPED");
// 		given(orderApiClient.updateBulkOrderStatus(any(OrderBulkUpdateRequest.class)))
// 			.willReturn(ResponseEntity.ok().build());
//
// 		// when & then
// 		mockMvc.perform(post("/admin/orders/bulk-status")
// 				.with(csrf()) // CSRF 토큰 추가
// 				.with(user("admin").roles("ADMIN")) // ADMIN 권한으로 인증
// 				.contentType(MediaType.APPLICATION_JSON)
// 				.content(objectMapper.writeValueAsString(request)))
// 			.andExpect(status().is3xxRedirection())
// 			.andExpect(redirectedUrl("/admin/orders"));
//
// 		verify(orderApiClient, times(1)).updateBulkOrderStatus(any(OrderBulkUpdateRequest.class));
// 	}
// }