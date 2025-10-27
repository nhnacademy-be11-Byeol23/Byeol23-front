package com.nhnacademy.byeol23front.orderset.order.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.nhnacademy.byeol23front.orderset.order.dto.OrderCancelRequest;
import com.nhnacademy.byeol23front.orderset.order.dto.OrderCreateResponse;
import com.nhnacademy.byeol23front.orderset.order.dto.OrderInfoResponse;
import com.nhnacademy.byeol23front.orderset.order.dto.OrderPrepareRequest;
import com.nhnacademy.byeol23front.orderset.order.dto.OrderPrepareResponse;

@FeignClient(name = "orderApiClient", url = "${gateway.api.url}")
public interface OrderApiClient {

	@PostMapping(value = "/api/orders")
	ResponseEntity<OrderPrepareResponse> prepareOrder(@RequestBody OrderPrepareRequest request);

	@PutMapping("/api/orders")
	ResponseEntity<OrderCreateResponse> updateOrderStatus(@RequestParam String orderNumber);

	@GetMapping("/api/orders")
	List<OrderInfoResponse> getAllOrders();

	@PostMapping("/api/orders/{orderNumber}")
	ResponseEntity<String> cancelOrder(@PathVariable String orderNumber, @RequestBody OrderCancelRequest request);

}
