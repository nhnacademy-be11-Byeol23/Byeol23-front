package com.nhnacademy.byeol23front.orderset.order.controller;

import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nhnacademy.byeol23front.common.ShopFeignClient;
import com.nhnacademy.byeol23front.orderset.order.dto.OrderCreateRequest;
import com.nhnacademy.byeol23front.orderset.order.dto.OrderCreateResponse;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

	private final ShopFeignClient feignClient;

	@GetMapping
	public String getOrder() {
		return "order/checkout";
	}

	@PostMapping("/checkout")
	@ResponseBody
	public ResponseEntity<OrderCreateResponse> processOrder(@RequestBody OrderCreateRequest request) {
		return feignClient.createOrder(request);
	}


}
