package com.nhnacademy.byeol23front.orderset.order.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
import com.nhnacademy.byeol23front.orderset.order.dto.OrderBulkUpdateRequest;
import com.nhnacademy.byeol23front.orderset.order.dto.OrderCancelRequest;
import com.nhnacademy.byeol23front.orderset.order.dto.OrderDetailResponse;
import com.nhnacademy.byeol23front.orderset.order.dto.OrderInfoResponse;
import com.nhnacademy.byeol23front.orderset.order.dto.OrderSearchCondition;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class OrderAdminController {
	private final OrderApiClient orderApiClient;
	private final OrderUtil orderUtil;

	@PostMapping("/{orderNumber}/cancel")
	public String cancelOrder(@PathVariable String orderNumber,
		@RequestBody OrderCancelRequest request) {
		orderApiClient.cancelOrder(orderNumber, request);

		return "redirect:/admin/orders";
	}

	@GetMapping("/{order-number}")
	public String getOrderByOrderNumber(@PathVariable(name = "order-number") String orderNumber, Model model) {
		ResponseEntity<OrderDetailResponse> response = orderApiClient.getOrderByOrderNumber(orderNumber);
		log.info("order: {}", response.getBody());
		model.addAttribute("orderDetail", response.getBody());
		orderUtil.addFinalPaymentAmountToModel(model, response.getBody());

		return "admin/order/order-detail";
	}

	@GetMapping
	public String getOrderMain(@RequestParam(name = "status", required = false) String status,
		@RequestParam(name = "orderNumber", required = false) String orderNumber,
		@RequestParam(name = "receiver", required = false) String receiver,
		@PageableDefault(size = 10) Pageable pageable,
		Model model) {

		OrderSearchCondition orderSearchCondition = new OrderSearchCondition(status, orderNumber, receiver);
		ResponseEntity<Page<OrderInfoResponse>> results = orderApiClient.searchOrders(orderSearchCondition, pageable);

		model.addAttribute("orders", results.getBody());

		return "admin/order/order";
	}

	@PostMapping("/bulk-status")
	public String updateBulkOrderStatus(@RequestBody OrderBulkUpdateRequest request) {
		orderApiClient.updateBulkOrderStatus(request);
		return "redirect:/admin/orders";
	}
}
