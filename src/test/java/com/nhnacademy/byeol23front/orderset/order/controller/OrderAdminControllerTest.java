package com.nhnacademy.byeol23front.orderset.order.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Collections;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.byeol23front.auth.AuthHelper;
import com.nhnacademy.byeol23front.bookset.book.dto.BookOrderInfoResponse;
import com.nhnacademy.byeol23front.bookset.category.client.CategoryApiClient;
import com.nhnacademy.byeol23front.orderset.delivery.dto.DeliveryPolicyInfoResponse;
import com.nhnacademy.byeol23front.orderset.order.client.OrderApiClient;
import com.nhnacademy.byeol23front.orderset.order.dto.OrderBulkUpdateRequest;
import com.nhnacademy.byeol23front.orderset.order.dto.OrderCancelRequest;
import com.nhnacademy.byeol23front.orderset.order.dto.OrderDetailResponse;
import com.nhnacademy.byeol23front.orderset.order.dto.OrderInfoResponse;
import com.nhnacademy.byeol23front.orderset.order.dto.OrderSearchCondition;
import com.nhnacademy.byeol23front.orderset.packaging.dto.PackagingInfoResponse;

import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;

@WebMvcTest(OrderAdminController.class)
@Import(AuthHelper.class)
class OrderAdminControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private OrderApiClient orderApiClient;

	@MockBean
	private CategoryApiClient categoryApiClient;

	@MockBean
	private AuthHelper authHelper;

	@MockBean
	private OrderUtil orderUtil; // OrderUtil은 Mock으로 처리하거나 실제 구현체를 주입해야 합니다.

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final String TEST_ORDER_NUMBER = "ORD-20231125-0001";
	private OrderCancelRequest cancelRequest;
	private OrderDetailResponse detailResponse;
	private OrderBulkUpdateRequest bulkUpdateRequest;


	@BeforeEach
	void setUp() {
		// 주문 취소 요청 DTO 설정
		cancelRequest = new OrderCancelRequest(TEST_ORDER_NUMBER);

		// Nested DTOs (based on user's new DTO definitions)
		PackagingInfoResponse packagingInfo = new PackagingInfoResponse(
			1L, "선물 포장", new BigDecimal("1000.00"), "packaging-img-url"
		);

		// BookOrderInfoResponse가 실제 DTO이므로 이를 사용합니다.
		BookOrderInfoResponse bookOrderInfo = new BookOrderInfoResponse(
			100L, "테스트 도서 A", 2, new BigDecimal("4500.00"), packagingInfo
		);

		// DeliveryPolicyInfoResponse가 실제 DTO이므로 이를 사용합니다.
		DeliveryPolicyInfoResponse deliveryPolicy = new DeliveryPolicyInfoResponse(
			new BigDecimal("50000.00"), new BigDecimal("3000.00"), LocalDateTime.of(2023, 1, 1, 0, 0)
		);

		List<BookOrderInfoResponse> items = List.of(bookOrderInfo);

		// 주문 상세 응답 DTO 설정 (업데이트된 구조 적용)
		detailResponse = new OrderDetailResponse(
			TEST_ORDER_NUMBER,
			LocalDateTime.of(2023, 11, 25, 10, 0, 0),
			"READY",
			new BigDecimal("10000.00"), // actualOrderPrice
			"Receiver Name",
			"010-1234-5678",
			"서울시 강남구", // receiverAddress
			"역삼동 123-45", // receiverAddressDetail
			"06130", // postCode
			items, // 캐스팅 없이 바로 사용
			deliveryPolicy, // 캐스팅 없이 바로 사용
			new BigDecimal("1000.00") // usedPoints
		);
		// 벌크 상태 변경 요청 DTO 설정
		bulkUpdateRequest = new OrderBulkUpdateRequest(
			Collections.singletonList(TEST_ORDER_NUMBER), "DELIVERING"
		);
	}

	@Test
	@DisplayName("POST /admin/orders/{orderNumber}/cancel - 주문 취소 성공 및 리다이렉트")
	void cancelOrder_success() throws Exception {
		// given
		// orderApiClient.cancelOrder가 ResponseEntity<Void>를 반환한다고 가정하고 Mocking을 수정합니다.
		when(orderApiClient.cancelOrder(eq(TEST_ORDER_NUMBER), any(OrderCancelRequest.class)))
			.thenReturn(ResponseEntity.ok().build());

		// when & then
		mockMvc.perform(post("/admin/orders/{orderNumber}/cancel", TEST_ORDER_NUMBER)
				.with(csrf())
				.with(user("admin").roles("ADMIN"))
				// 인증된 ADMIN 권한으로 요청
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(cancelRequest)))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/admin/orders"));

		// verify(orderApiClient).cancelOrder는 이제 when 절에 포함되어 있으므로 그대로 둡니다.
		verify(orderApiClient).cancelOrder(eq(TEST_ORDER_NUMBER), any(OrderCancelRequest.class));
	}
	@Test
	@DisplayName("GET /admin/orders/{order-number} - 주문 상세 조회 성공 및 뷰 반환")
	void getOrderByOrderNumber_success() throws Exception {
		// given
		when(orderApiClient.getOrderByOrderNumber(TEST_ORDER_NUMBER))
			.thenReturn(new ResponseEntity<>(detailResponse, HttpStatus.OK));

		// orderUtil.addFinalPaymentAmountToModel 호출 시 model에 attribute가 추가되도록 가정
		doNothing().when(orderUtil).addFinalPaymentAmountToModel(any(), any());

		// when & then
		mockMvc.perform(get("/admin/orders/{order-number}", TEST_ORDER_NUMBER)
				.with(csrf())
				.with(user("admin").roles("ADMIN")))
			.andExpect(status().isOk())
			.andExpect(view().name("admin/order/order-detail"))
			.andExpect(model().attributeExists("orderDetail"));

		verify(orderApiClient).getOrderByOrderNumber(TEST_ORDER_NUMBER);
		verify(orderUtil).addFinalPaymentAmountToModel(any(), eq(detailResponse));
	}

	@Test
	@DisplayName("GET /admin/orders - 주문 목록 조회 및 검색 성공")
	void getOrderMain_success_with_search() throws Exception {
		// given
		String status = "READY";
		String orderNumber = "ORD-123";
		String receiver = "John Doe";
		// 컨트롤러가 생성할 것으로 예상되는 정확한 SearchCondition 객체를 생성합니다.
			Pageable pageable = PageRequest.of(0, 10); // 기본 페이지 요청 (MockMvc가 전달할 값)

		PageImpl<OrderInfoResponse> mockPage = new PageImpl<>(
			Collections.singletonList(new OrderInfoResponse(TEST_ORDER_NUMBER,LocalDateTime.now(),  receiver, new BigDecimal("10000") , status)),
			pageable, 1
		);

		// Argument Captor 선언 (두 개의 캡처를 사용)
		ArgumentCaptor<OrderSearchCondition> conditionCaptor = ArgumentCaptor.forClass(OrderSearchCondition.class);
		ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class); // Pageable 캡처 추가

		// Mocking: any()를 사용하여 Mocking이 확실히 작동하도록 인자 일치 조건을 완화합니다.
		when(orderApiClient.searchOrders(any(OrderSearchCondition.class), any(Pageable.class)))
			.thenReturn(new ResponseEntity<>(mockPage, HttpStatus.OK));

		// when & then
		// 인증된 ADMIN 권한으로 요청 추가
		mockMvc.perform(get("/admin/orders")
				.with(csrf())
				.with(user("admin").roles("ADMIN"))
				.param("status", status)
				.param("orderNumber", orderNumber)
				.param("receiver", receiver)
				.param("page", "0")
				.param("size", "10"))

			.andExpect(status().isOk())
			.andExpect(view().name("admin/order/order"))
			.andExpect(model().attributeExists("orders"));

		// 검증:
		// 1. searchOrders가 호출되었는지 확인하고, 두 인자를 모두 캡처합니다.
		// Pageable 인자 검증을 위해 캡처를 사용합니다.
		verify(orderApiClient).searchOrders(conditionCaptor.capture(), pageableCaptor.capture());

		// 2. 캡처된 OrderSearchCondition 객체의 필드 값들을 명시적으로 검증합니다.
		OrderSearchCondition capturedCondition = conditionCaptor.getValue();
		assertEquals(status, capturedCondition.getStatus());
		assertEquals(orderNumber, capturedCondition.getOrderNumber());
		assertEquals(receiver, capturedCondition.getReceiver());

		// 3. 캡처된 Pageable 객체의 필드 값들을 명시적으로 검증합니다.
		Pageable capturedPageable = pageableCaptor.getValue();
		assertEquals(pageable.getPageNumber(), capturedPageable.getPageNumber());
		assertEquals(pageable.getPageSize(), capturedPageable.getPageSize());	}

	@Test
	@DisplayName("POST /admin/orders/bulk-status - 주문 상태 벌크 업데이트 성공 (200 OK)")
	void updateBulkOrderStatus_success() throws Exception {
		// given
		when(orderApiClient.updateBulkOrderStatus(any(OrderBulkUpdateRequest.class)))
			.thenReturn(new ResponseEntity<>(HttpStatus.OK));

		// when & then
		mockMvc.perform(post("/admin/orders/bulk-status")
				.with(csrf())
				.with(user("admin").roles("ADMIN"))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(bulkUpdateRequest)))
			.andExpect(status().isOk());

		verify(orderApiClient).updateBulkOrderStatus(any(OrderBulkUpdateRequest.class));
	}

	@Test
	@DisplayName("POST /admin/orders/bulk-status - Feign 예외 발생 시 해당 상태 코드 반환 (400 Bad Request)")
	void updateBulkOrderStatus_feign_exception() throws Exception {
		// given
		int feignStatus = 400;
		FeignException mockException = new FeignException.BadRequest(
			"Bad Request",
			Request.create(Request.HttpMethod.POST, "/api", Collections.emptyMap(), null, new RequestTemplate()),
			null,
			Collections.emptyMap()
		);

		doThrow(mockException).when(orderApiClient).updateBulkOrderStatus(any(OrderBulkUpdateRequest.class));

		// when & then
		mockMvc.perform(post("/admin/orders/bulk-status")
				.with(csrf())
				.with(user("admin").roles("ADMIN"))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(bulkUpdateRequest)))
			.andExpect(status().isBadRequest()); // 400

		verify(orderApiClient).updateBulkOrderStatus(any(OrderBulkUpdateRequest.class));
	}

	@Test
	@DisplayName("POST /admin/orders/bulk-status - 일반 예외 발생 시 500 Internal Server Error 반환")
	void updateBulkOrderStatus_general_exception() throws Exception {
		// given
		doThrow(new RuntimeException("Unexpected Error")).when(orderApiClient).updateBulkOrderStatus(any(OrderBulkUpdateRequest.class));

		// when & then
		mockMvc.perform(post("/admin/orders/bulk-status")
				.with(csrf())
				.with(user("admin").roles("ADMIN"))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(bulkUpdateRequest)))
			.andExpect(status().isInternalServerError()); // 500

		verify(orderApiClient).updateBulkOrderStatus(any(OrderBulkUpdateRequest.class));
	}
}