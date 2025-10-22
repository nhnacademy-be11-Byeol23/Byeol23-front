package com.nhnacademy.byeol23front.orderset.order.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nhnacademy.byeol23front.orderset.order.client.OrderApiClient;
import com.nhnacademy.byeol23front.orderset.order.dto.OrderCreateRequest;
import com.nhnacademy.byeol23front.orderset.order.dto.OrderCreateResponse;
import com.nhnacademy.byeol23front.orderset.order.dto.OrderPrepareRequest;
import com.nhnacademy.byeol23front.orderset.order.dto.OrderPrepareResponse;
import com.nhnacademy.byeol23front.orderset.order.dto.PaymentParamResponse;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

	private final OrderApiClient orderApiClient;

	@GetMapping
	public String getOrder() {
		return "order/checkout";
	}

	@PostMapping("/prepare")
	@ResponseBody
	public ResponseEntity<OrderPrepareResponse>prepareOrder(@RequestBody OrderPrepareRequest request) {
		ResponseEntity<OrderPrepareResponse> response = orderApiClient.prepareOrder(request);
		return response;
	}


	@GetMapping("/success")
	public String orderSuccess(@RequestParam String orderId,
		@RequestParam String paymentKey,
		@RequestParam int amount) {

		PaymentParamResponse paymentParamResponse = new PaymentParamResponse(orderId, paymentKey, amount);

		return "order/success";
	}

	@GetMapping("/fail")
	public String orderFail() {
		return "order/fail";
	}


}
