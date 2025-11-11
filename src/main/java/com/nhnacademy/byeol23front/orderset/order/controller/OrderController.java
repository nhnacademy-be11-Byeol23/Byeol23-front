package com.nhnacademy.byeol23front.orderset.order.controller;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nhnacademy.byeol23front.bookset.book.dto.BookInfoRequest;
import com.nhnacademy.byeol23front.bookset.book.dto.BookOrderRequest;
import com.nhnacademy.byeol23front.minio.service.MinioService;
import com.nhnacademy.byeol23front.orderset.delivery.client.DeliveryApiClient;
import com.nhnacademy.byeol23front.orderset.delivery.dto.DeliveryPolicyInfoResponse;
import com.nhnacademy.byeol23front.orderset.order.client.OrderApiClient;
import com.nhnacademy.byeol23front.orderset.order.dto.OrderPrepareRequest;
import com.nhnacademy.byeol23front.orderset.order.dto.OrderPrepareResponse;
import com.nhnacademy.byeol23front.orderset.order.dto.PointOrderResponse;
import com.nhnacademy.byeol23front.orderset.order.exception.OrderPrepareFailException;
import com.nhnacademy.byeol23front.orderset.packaging.client.PackagingApiClient;
import com.nhnacademy.byeol23front.orderset.packaging.dto.PackagingInfoResponse;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
	private final OrderApiClient orderApiClient;
	private final DeliveryApiClient deliveryApiClient;
	private final PackagingApiClient packagingApiClient;
	private final MinioService minioService;

	@PostMapping("/direct")
	@ResponseBody
	public ResponseEntity<Void> handleDirectOrder(@RequestBody BookOrderRequest request,
		HttpSession session) {

		session.setAttribute("directOrderRequest", request);

		return ResponseEntity.ok().build();
	}

	@GetMapping("/direct")
	public String getOrderFormDirect(HttpSession session, Model model) {
		BookOrderRequest request = (BookOrderRequest)session.getAttribute("directOrderRequest");
		List<PackagingInfoResponse> packagingOptions = packagingApiClient.getAllPackagingLists();

		if (Objects.isNull(request)) {
			throw new IllegalArgumentException("주문 정보가 없습니다.");
		}

		session.removeAttribute("directOrderRequest");

		addTotalQuantity(model, request.bookList());
		addDeliveryDatesToModel(model);
		addOrderSummary(model, request.bookList());
		addDeliveryFeeToModel(model, request);
		

		model.addAttribute("packagingOptions", packagingOptions);
		model.addAttribute("userPoint", 300_000);

		return "order/checkout";
	}

	@GetMapping
	public String getOrder(Model model) {
		addDeliveryDatesToModel(model);

		BigDecimal totalBookPrice = new BigDecimal(298000);

		ResponseEntity<DeliveryPolicyInfoResponse> response = deliveryApiClient.getCurrentDeliveryPolicy();
		DeliveryPolicyInfoResponse deliveryPolicy = response.getBody();

		BigDecimal deliveryFee = BigDecimal.ZERO;
		BigDecimal actualOrderPrice = totalBookPrice;

		if (deliveryPolicy != null) {
			BigDecimal policyFee = deliveryPolicy.deliveryFee();
			BigDecimal freeThreshold = deliveryPolicy.freeDeliveryCondition();

			if (freeThreshold != null && freeThreshold.compareTo(BigDecimal.ZERO) > 0
				&& totalBookPrice.compareTo(freeThreshold) >= 0) {
				deliveryFee = BigDecimal.ZERO;
			} else {
				deliveryFee = policyFee != null ? policyFee : BigDecimal.ZERO;
			}
		} else {
			log.warn("배송비 정책을 가져올 수 없습니다. 기본 배송비 0원으로 처리합니다.");
		}

		actualOrderPrice = totalBookPrice.add(deliveryFee);

		model.addAttribute("totalBookPrice", totalBookPrice);
		model.addAttribute("deliveryFee", deliveryFee);
		model.addAttribute("actualOrderPrice", actualOrderPrice);

		model.addAttribute("userPoint", 3000000);

		return "order/checkout";
	}

	@PostMapping("/prepare")
	@ResponseBody
	public ResponseEntity<OrderPrepareResponse> prepareOrder(@RequestBody OrderPrepareRequest request) {
		ResponseEntity<OrderPrepareResponse> response = orderApiClient.prepareOrder(request);
		log.info("주문 준비 응답: {}", response.getBody());

		if (!response.getStatusCode().is2xxSuccessful()) {
			log.error("주문 준비 실패: {}", response.getStatusCode());
			throw new OrderPrepareFailException("주문 임시 저장에 실패했습니다.");
		}

		return response;
	}

	@GetMapping("/success")
	public String getOrderWithPoints(@RequestParam String orderNumber,
		Model model) {
		ResponseEntity<PointOrderResponse> responseEntity = orderApiClient.saveOrderWithPoints(orderNumber);
		PointOrderResponse savedPaymentInfo = responseEntity.getBody();

		if (savedPaymentInfo == null) {
			log.error("백엔드에서 포인트 결제 정보를 받아오지 못했습니다. orderNumber: {}", orderNumber);
			model.addAttribute("status", 500);
			model.addAttribute("error", "Backend Response Error");
			model.addAttribute("message", "포인트 결제 내역 저장 후 정보를 받아오지 못했습니다.");
			return "error";
		}

		Map<String, Object> paymentInfo = new HashMap<>();
		paymentInfo.put("orderId", savedPaymentInfo.orderNumber());
		paymentInfo.put("totalAmount", BigDecimal.valueOf(savedPaymentInfo.totalAmount().longValue()));
		paymentInfo.put("method", savedPaymentInfo.method());

		model.addAttribute("orderId", savedPaymentInfo.orderNumber());
		model.addAttribute("paymentInfo", paymentInfo);

		return "order/success";
	}

	private void addDeliveryDatesToModel(Model model) {
		List<Map<String, String>> deliveryDate = new ArrayList<>();
		LocalDate today = LocalDate.now();
		LocalDate currentDate = today;
		int businessDaysCount = 0;
		LocalDate defaultDate = null;

		DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("M/d"); // 10/30
		DateTimeFormatter valueFormatter = DateTimeFormatter.ISO_LOCAL_DATE;

		while (businessDaysCount < 5) {
			currentDate = currentDate.plusDays(1);
			DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
			if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
				businessDaysCount++;
				String dayName = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN); // e.g 목
				String displayDate = currentDate.format(displayFormatter); // e.g 10/30
				String valueDate = currentDate.format(valueFormatter); // e.g 2025-10-30

				deliveryDate.add(Map.of(
					"dayName", dayName,
					"displayDate", displayDate,
					"valueDate", valueDate
				));

				if (businessDaysCount == 2) {
					defaultDate = currentDate;
				}
			}
		}

		model.addAttribute("deliveryDates", deliveryDate);
		model.addAttribute("defaultDeliveryDate", defaultDate != null ? defaultDate.format(valueFormatter) : "");
	}

	private void addDeliveryFeeToModel(Model model, BookOrderRequest request) {

		BigDecimal totalBookPrice = BigDecimal.ZERO;

		for (BookInfoRequest infoRequest : request.bookList()) {
			BigDecimal quantity = BigDecimal.valueOf(infoRequest.quantity());
			BigDecimal itemSubtotal = infoRequest.salePrice().multiply(quantity);
			totalBookPrice = totalBookPrice.add(itemSubtotal);
		}

		ResponseEntity<DeliveryPolicyInfoResponse> response = deliveryApiClient.getCurrentDeliveryPolicy();
		DeliveryPolicyInfoResponse deliveryPolicy = response.getBody();

		BigDecimal deliveryFee = BigDecimal.ZERO;
		BigDecimal actualOrderPrice = totalBookPrice;

		if (deliveryPolicy != null) {
			BigDecimal policyFee = deliveryPolicy.deliveryFee();
			BigDecimal freeThreshold = deliveryPolicy.freeDeliveryCondition();

			if (freeThreshold != null && freeThreshold.compareTo(BigDecimal.ZERO) > 0
				&& totalBookPrice.compareTo(freeThreshold) >= 0) {
				deliveryFee = BigDecimal.ZERO;
			} else {
				deliveryFee = policyFee != null ? policyFee : BigDecimal.ZERO;
			}
		} else {
			log.warn("배송비 정책을 가져올 수 없습니다. 기본 배송비 0원으로 처리합니다.");
		}

		actualOrderPrice = totalBookPrice.add(deliveryFee);

		model.addAttribute("totalBookPrice", totalBookPrice);
		model.addAttribute("deliveryFee", deliveryFee);
		model.addAttribute("actualOrderPrice", actualOrderPrice);
	}

	private void addOrderSummary(Model model, List<BookInfoRequest> requestList) {
		model.addAttribute("orderItem", requestList);
	}

	private void addTotalQuantity(Model model, List<BookInfoRequest> requestList) {
		int totalQuantity = requestList.stream()
			.mapToInt(BookInfoRequest::quantity)
			.sum();

		model.addAttribute("totalQuantity", totalQuantity);
	}
}
