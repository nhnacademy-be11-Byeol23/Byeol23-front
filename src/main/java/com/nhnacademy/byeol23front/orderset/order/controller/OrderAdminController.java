package com.nhnacademy.byeol23front.orderset.order.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.nhnacademy.byeol23front.orderset.order.client.OrderApiClient;
import com.nhnacademy.byeol23front.orderset.order.dto.OrderCancelRequest;
import com.nhnacademy.byeol23front.orderset.order.dto.OrderInfoResponse;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class OrderAdminController {
	private final OrderApiClient orderApiClient;

	@GetMapping
	public String getAllOrders(Model model) {
		List<OrderInfoResponse> orderInfoResponses = orderApiClient.getAllOrders();
		model.addAttribute("orders", orderInfoResponses);

		return "admin/order/order";
	}

	@PostMapping("/{orderNumber}/cancel")
	public String cancelOrder(@PathVariable String orderNumber,
		@RequestBody OrderCancelRequest request) {
		orderApiClient.cancelOrder(orderNumber, request);
		return "redirect:/admin/orders";
	}
}
