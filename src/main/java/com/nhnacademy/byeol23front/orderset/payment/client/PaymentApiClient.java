package com.nhnacademy.byeol23front.orderset.payment.client;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.nhnacademy.byeol23front.orderset.payment.dto.PaymentCancelRequest;
import com.nhnacademy.byeol23front.orderset.payment.dto.PaymentParamRequest;

@FeignClient(name = "BYEOL23-GATEWAY", contextId = "paymentApiClient")
public interface PaymentApiClient {

	@PostMapping("/api/payments/confirm")
	ResponseEntity<String> confirmPayment(@RequestBody PaymentParamRequest paymentParamRequest);

	@PostMapping("/api/payments/cancel")
	ResponseEntity<String> cancelPayment(@RequestBody PaymentCancelRequest paymentCancelRequest);

	@PostMapping("/api/payments")
	void createPayment(@RequestBody Map<String, Object> responseMap);
}
