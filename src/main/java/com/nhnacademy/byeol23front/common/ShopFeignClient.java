package com.nhnacademy.byeol23front.common;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.nhnacademy.byeol23front.orderset.order.dto.OrderCreateRequest;
import com.nhnacademy.byeol23front.orderset.order.dto.OrderCreateResponse;

@FeignClient(name = "shopFeignClient", url = "http://localhost:8080")
public interface ShopFeignClient {

	@PostMapping(value = "/api/order")
	ResponseEntity<OrderCreateResponse> createOrder(@RequestBody OrderCreateRequest request);

}
