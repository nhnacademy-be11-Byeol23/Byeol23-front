package com.nhnacademy.byeol23front.orderset.payment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.byeol23front.auth.AuthHelper;
import com.nhnacademy.byeol23front.bookset.category.client.CategoryApiClient;
import com.nhnacademy.byeol23front.orderset.order.client.OrderApiClient;
import com.nhnacademy.byeol23front.orderset.payment.client.PaymentApiClient;
import com.nhnacademy.byeol23front.orderset.payment.dto.PaymentCancelRequest;
import com.nhnacademy.byeol23front.orderset.payment.dto.PaymentParamRequest;
import com.nhnacademy.byeol23front.orderset.payment.dto.PaymentResultResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
@Import(AuthHelper.class)
class PaymentControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private PaymentApiClient paymentApiClient;
	@MockBean
	private OrderApiClient orderApiClient;
	@MockBean
	private AuthHelper authHelper;

	// (Context Load를 위한 @ControllerAdvice 의존성 Mocking)
	@MockBean
	private CategoryApiClient categoryApiClient;

	// --- 테스트용 공통 데이터 ---
	private String orderId = "test-order-123";
	private String paymentKey = "paymentKey-abc";
	private BigDecimal amount = new BigDecimal("15000");

	private PaymentParamRequest paymentParamRequest;
	private PaymentResultResponse paymentResultResponse;

	@BeforeEach
	void setUp() {
		paymentParamRequest = new PaymentParamRequest(orderId, paymentKey, amount);

		paymentResultResponse = new PaymentResultResponse(
			paymentKey, orderId, "테스트 주문", "DONE", amount,
			LocalDateTime.now().minusMinutes(1), LocalDateTime.now(), "TOSS_PAYMENTS"
		);
	}

	@Test
	@DisplayName("GET /payments/confirm - 결제 승인 및 금액 검증 성공")
	void paymentConfirm_Success_AmountVerified() throws Exception {
		// given
		// 1. 결제 승인 API (paymentApiClient.confirmPayment) - 성공
		given(paymentApiClient.confirmPayment(any(PaymentParamRequest.class)))
			.willReturn(ResponseEntity.ok(paymentResultResponse));

		// 2. 결제 정보 생성 API (paymentApiClient.createPayment) - 성공
		doNothing().when(paymentApiClient).createPayment(any(PaymentResultResponse.class));
		// 3. 주문 상태 변경 API (orderApiClient.updateOrderStatus) - 성공
		given(orderApiClient.updateOrderStatus(eq(orderId), eq("결제 완료")))
			.willReturn(ResponseEntity.ok().build());

		// when & then
		mockMvc.perform(get("/payments/confirm")
					.with(csrf())
					.with(user("admin").roles("ADMIN"))
					.param("orderId", orderId)
					.param("paymentKey", paymentKey)
					.param("amount", amount.toString())
				// (Toss 콜백이므로 인증이 필요 없다고 가정, 필요시 .with(user(...)) 추가)
			)
			.andExpect(status().isOk())
			.andExpect(view().name("order/success")) // 성공 뷰
			.andExpect(model().attributeExists("orderId", "paymentInfo"))
			.andExpect(model().attribute("orderId", orderId));

		// verify
		verify(paymentApiClient, times(1)).confirmPayment(any(PaymentParamRequest.class));
		verify(paymentApiClient, times(1)).createPayment(any(PaymentResultResponse.class));
		verify(orderApiClient, times(1)).updateOrderStatus(orderId, "결제 완료");
		verify(paymentApiClient, never()).cancelPayment(any()); // 취소는 호출되지 않음
	}

	@Test
	@DisplayName("GET /payments/confirm - 결제 승인 성공, '금액 위변조' 의심 (자동 취소 성공)")
	void paymentConfirm_AmountMismatch_CancelSuccess() throws Exception {
		// given
		BigDecimal requestedAmount = new BigDecimal("15000"); // 사용자가 요청한 금액
		BigDecimal approvedAmount = new BigDecimal("10000"); // PG사가 실제로 승인한 금액 (다름)

		PaymentResultResponse mismatchedResponse = new PaymentResultResponse(
			paymentKey, orderId, "테스트 주문", "DONE", approvedAmount, // <-- 금액이 다름
			LocalDateTime.now().minusMinutes(1), LocalDateTime.now(), "TOSS_PAYMENTS"
		);

		// 1. 결제 승인 API는 성공 (금액이 다른 DTO 반환)
		given(paymentApiClient.confirmPayment(any(PaymentParamRequest.class)))
			.willReturn(ResponseEntity.ok(mismatchedResponse));

		// 2. '금액 검증 실패'로 인한 '결제 취소' API 호출 -> 성공
		given(paymentApiClient.cancelPayment(any(PaymentCancelRequest.class)))
			.willReturn(ResponseEntity.ok("결제 취소 성공"));

		// when & then
		mockMvc.perform(get("/payments/confirm")
				.with(csrf())
				.with(user("admin").roles("ADMIN"))
				.param("orderId", orderId)
				.param("paymentKey", paymentKey)
				.param("amount", requestedAmount.toString())) // <-- 요청 금액은 15000
			.andExpect(status().isOk())
			.andExpect(view().name("error")) // error 뷰
			.andExpect(model().attribute("error", "Payment Validation Failed"))
			.andExpect(model().attribute("message", "결제 금액 검증에 실패하여 결제를 자동으로 취소했습니다."));

		// verify
		verify(paymentApiClient, times(1)).confirmPayment(any(PaymentParamRequest.class));
		verify(paymentApiClient, times(1)).cancelPayment(any(PaymentCancelRequest.class)); // 취소 API가 호출됨
		verify(paymentApiClient, never()).createPayment(any()); // 결제 생성은 호출되지 않음
		verify(orderApiClient, never()).updateOrderStatus(anyString(), anyString()); // 주문 상태 변경은 호출되지 않음
	}

	@Test
	@DisplayName("GET /payments/confirm - 결제 승인 실패 (API 4xx/5xx)")
	void paymentConfirm_ApiFails_ReturnsErrorPage() throws Exception {
		// given
		// 1. 결제 승인 API 자체가 500 에러를 반환
		given(paymentApiClient.confirmPayment(any(PaymentParamRequest.class)))
			.willThrow(new RuntimeException("API 호출 실패")); // (FeignException 등 구체적인 예외도 가능)

		// when & then
		mockMvc.perform(get("/payments/confirm")
				.with(csrf())
				.with(user("admin").roles("ADMIN"))
				.param("orderId", orderId)
				.param("paymentKey", paymentKey)
				.param("amount", amount.toString()))
			.andExpect(status().isOk()) // 컨트롤러가 예외를 잡아서 200 OK
			.andExpect(view().name("error")) // error 뷰
			.andExpect(model().attribute("status", 500))
			.andExpect(model().attribute("error", "Internal Server Error"));

		// verify
		verify(paymentApiClient, times(1)).confirmPayment(any(PaymentParamRequest.class));
		verify(paymentApiClient, never()).cancelPayment(any()); // 취소 API 호출 안 됨
		verify(paymentApiClient, never()).createPayment(any()); // 생성 API 호출 안 됨
	}

	@Test
	@DisplayName("GET /payments/confirm - 승인 상태가 'DONE'이 아닌 경우")
	void paymentConfirm_StatusNotDone_ReturnsErrorPage() throws Exception {
		// given
		paymentResultResponse = new PaymentResultResponse(
			paymentKey, orderId, "테스트 주문", "WAITING", amount, // <-- 상태가 DONE이 아님
			LocalDateTime.now().minusMinutes(1), LocalDateTime.now(), "TOSS_PAYMENTS"
		);

		given(paymentApiClient.confirmPayment(any(PaymentParamRequest.class)))
			.willReturn(ResponseEntity.ok(paymentResultResponse));

		// when & then
		mockMvc.perform(get("/payments/confirm")
				.with(csrf())
				.with(user("admin").roles("ADMIN"))
				.param("orderId", orderId)
				.param("paymentKey", paymentKey)
				.param("amount", amount.toString()))
			.andExpect(status().isOk())
			.andExpect(view().name("error"))
			.andExpect(model().attribute("error", "Payment Status Error"))
			.andExpect(model().attribute("message", "결제 승인 상태 확인에 실패했습니다: WAITING"));
	}

	@Test
	@DisplayName("GET /payments/fail - 결제 실패 페이지 로드")
	void orderFail_Success() throws Exception {
		// given
		String errorCode = "PAYMENT_CANCELLED";
		String errorMessage = "사용자가 결제를 취소했습니다.";

		// when & then
		mockMvc.perform(get("/payments/fail")
				.with(csrf())
				.with(user("admin").roles("ADMIN"))
				.param("code", errorCode)
				.param("message", errorMessage))
			.andExpect(status().isOk())
			.andExpect(view().name("error"))
			.andExpect(model().attribute("status", 400))
			.andExpect(model().attribute("error", errorCode))
			.andExpect(model().attribute("message", errorMessage));
	}
}