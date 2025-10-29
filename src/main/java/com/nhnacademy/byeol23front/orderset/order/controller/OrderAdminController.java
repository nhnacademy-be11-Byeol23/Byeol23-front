package com.nhnacademy.byeol23front.orderset.order.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.nhnacademy.byeol23front.orderset.order.client.OrderApiClient;
import com.nhnacademy.byeol23front.orderset.order.dto.OrderCancelRequest;
import com.nhnacademy.byeol23front.orderset.order.dto.OrderDetailResponse;
import com.nhnacademy.byeol23front.orderset.order.dto.OrderInfoResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class OrderAdminController {
	private final OrderApiClient orderApiClient;

	@PostMapping("/{orderNumber}/cancel")
	public String cancelOrder(@PathVariable String orderNumber,
		@RequestBody OrderCancelRequest request) {
		orderApiClient.cancelOrder(orderNumber, request);
		return "redirect:/admin/orders";
	}

	@GetMapping("/{orderNumber}")
	public String getOrderByOrderNumber(@PathVariable String orderNumber, Model model) {
		ResponseEntity<OrderDetailResponse> response = orderApiClient.getOrderByOrderNumber(orderNumber);
		log.info("order: {}", response.getBody());
		model.addAttribute("orderDetail", response.getBody());

		return "admin/order/order-detail";
	}

	@GetMapping
	public String searchOrders(@RequestParam(name = "status", required = false) String status,
		@RequestParam(name = "orderNumber", required = false) String orderNumber,
		@RequestParam(name = "receiver", required = false) String receiver,
		Model model) {

		ResponseEntity<List<OrderInfoResponse>> filteredOrders = orderApiClient.searchOrders(status, orderNumber, receiver);

		model.addAttribute("orders", filteredOrders.getBody());
		return "admin/order/order";
	}
}
