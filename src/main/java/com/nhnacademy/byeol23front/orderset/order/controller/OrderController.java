package com.nhnacademy.byeol23front.orderset.order.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nhnacademy.byeol23front.orderset.order.client.OrderApiClient;
import com.nhnacademy.byeol23front.orderset.order.dto.OrderPrepareRequest;
import com.nhnacademy.byeol23front.orderset.order.dto.OrderPrepareResponse;
import com.nhnacademy.byeol23front.orderset.order.exception.OrderPrepareFailException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
	private final OrderApiClient orderApiClient;

	@GetMapping
	public String getOrder() {
		return "order/checkout";
	}

	@PostMapping("/prepare")
	@ResponseBody
	public ResponseEntity<OrderPrepareResponse> prepareOrder(@RequestBody OrderPrepareRequest request) {
		ResponseEntity<OrderPrepareResponse> response = orderApiClient.prepareOrder(request);
		log.info("주문 준비 응답: {}", response.getBody());

		if(!response.getStatusCode().is2xxSuccessful()) {
			log.error("주문 준비 실패: {}", response.getStatusCode());
			throw new OrderPrepareFailException("주문 임시 저장에 실패했습니다.");
		}

		return response;
	}

}
