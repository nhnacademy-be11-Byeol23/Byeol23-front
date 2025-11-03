package com.nhnacademy.byeol23front.orderset.order.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.nhnacademy.byeol23front.orderset.order.dto.OrderCancelRequest;
import com.nhnacademy.byeol23front.orderset.order.dto.OrderCancelResponse;
import com.nhnacademy.byeol23front.orderset.order.dto.OrderCreateResponse;
import com.nhnacademy.byeol23front.orderset.order.dto.OrderDetailResponse;
import com.nhnacademy.byeol23front.orderset.order.dto.OrderInfoResponse;
import com.nhnacademy.byeol23front.orderset.order.dto.OrderPrepareRequest;
import com.nhnacademy.byeol23front.orderset.order.dto.OrderPrepareResponse;
import com.nhnacademy.byeol23front.orderset.order.dto.OrderSearchCondition;
import com.nhnacademy.byeol23front.orderset.order.dto.PointOrderResponse;

@FeignClient(name = "orderApiClient", url = "${backend.api.url}")
public interface OrderApiClient {

	@PostMapping(value = "/api/orders")
	ResponseEntity<OrderPrepareResponse> prepareOrder(@RequestBody OrderPrepareRequest request);

	@PutMapping("/api/orders")
	ResponseEntity<OrderCreateResponse> updateOrderStatus(@RequestParam String orderNumber);

	@PostMapping("/api/orders/{order-number}")
	ResponseEntity<OrderCancelResponse> cancelOrder(@PathVariable(name = "order-number") String orderNumber, OrderCancelRequest request);

	@GetMapping("/api/orders/{order-number}")
	ResponseEntity<OrderDetailResponse> getOrderByOrderNumber(@PathVariable(name = "order-number") String orderNumber);

	@GetMapping("/api/orders")
	ResponseEntity<Page<OrderInfoResponse>> searchOrders(@SpringQueryMap OrderSearchCondition orderSearchCondition, Pageable pageable);


	@PostMapping("/api/orders/points")
	ResponseEntity<PointOrderResponse> saveOrderWithPoints(@RequestParam String orderNumber);

}
