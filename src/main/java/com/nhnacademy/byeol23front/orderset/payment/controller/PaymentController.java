package com.nhnacademy.byeol23front.orderset.payment.controller;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.byeol23front.orderset.payment.client.PaymentApiClient;
import com.nhnacademy.byeol23front.orderset.payment.dto.PaymentCancelRequest;
import com.nhnacademy.byeol23front.orderset.payment.dto.PaymentParamRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
	private final PaymentApiClient paymentApiClient;
	private final ObjectMapper objectMapper;

	@GetMapping("/confirm")
	public String paymentConfirm(@RequestParam String orderId,
		@RequestParam String paymentKey,
		@RequestParam BigDecimal amount,
		Model model) {

		log.info("결제 성공 리다이렉션: {}, {}, {},", orderId, paymentKey, amount);

		try {
			PaymentParamRequest paymentParamRequest = new PaymentParamRequest(orderId, paymentKey, amount);
			ResponseEntity<String> response = paymentApiClient.confirmPayment(paymentParamRequest);

			log.info("토스페이먼츠 승인 API 응답 상태 코드: {}", response.getStatusCode());
			log.info("토스페이먼츠 승인 API 응답 본문: {}", response.getBody());

			if (response.getStatusCode().is2xxSuccessful()) {
				Map<String, Object> responseMap = objectMapper.readValue(response.getBody(), new TypeReference<Map<String, Object>>() {});

				if ("DONE".equals(responseMap.get("status"))) {
					Number approvedAmountNumber = (Number) responseMap.get("totalAmount");
					BigDecimal approvedAmount = BigDecimal.valueOf(approvedAmountNumber.longValue());
					if (amount.compareTo(approvedAmount) == 0) {
						log.info("결제 승인 및 금액 검증 성공! 주문 ID: {}", orderId);

						model.addAttribute("orderId", orderId);
						model.addAttribute("paymentInfo", responseMap);
						return "order/success";
					} else {
						log.error("금액 위변조 의심! 요청 금액: {}, 승인 금액: {}", amount, approvedAmount);
						try {
							PaymentCancelRequest cancelRequest = new PaymentCancelRequest("결제 금액 검증 실패", paymentKey);
							ResponseEntity<String> cancelResponse = paymentApiClient.cancelPayment(cancelRequest);

							if (cancelResponse.getStatusCode().is2xxSuccessful()) {
								log.info("결제 취소 성공! 주문 ID: {}, 사유: {}", orderId, "결제 금액 검증 실패");
								model.addAttribute("message", "결제 금액 검증에 실패하여 결제를 자동으로 취소했습니다.");
							} else {
								log.error("결제 취소 요청 실패! 상태 코드: {}, 응답: {}", cancelResponse.getStatusCode(), cancelResponse.getBody());
								model.addAttribute("message", "결제 금액 검증 실패 및 자동 취소 중 오류 발생. 관리자에게 문의하세요.");
							}
						} catch (Exception e) {
							log.error("결제 취소 api 호출 중 오류 발생: {}", e.getMessage());
							model.addAttribute("message", "결제 금액 검증 실패 및 자동 취소 중 시스템 오류 발생.");
						}

						return "order/fail";
					}
				} else {
					log.error("토스페이먼츠 승인 상태 오류. 상태: {}", responseMap.get("status"));
					model.addAttribute("message", "결제 승인 상태 확인에 실패했습니다: " + responseMap.get("status"));
					return "order/fail";
				}
			}

		} catch (Exception e) {
			log.error("결제 승인 처리 중 예외 발생", e);
			Thread.currentThread().interrupt();
			model.addAttribute("message", "결제 처리 중 시스템 오류 발생: " + e.getMessage());
			return "order/fail";
		}

		return "order/success";
	}

	@GetMapping("/fail")
	public String orderFail(Model model) {
		model.addAttribute("message", "결제 처리 중 시스템 오류 발생");
		return "order/fail";
	}


}
