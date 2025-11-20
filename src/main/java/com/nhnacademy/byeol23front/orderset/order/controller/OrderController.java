package com.nhnacademy.byeol23front.orderset.order.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nhnacademy.byeol23front.bookset.book.dto.BookOrderRequest;
import com.nhnacademy.byeol23front.orderset.delivery.client.DeliveryApiClient;
import com.nhnacademy.byeol23front.orderset.delivery.dto.DeliveryPolicyInfoResponse;
import com.nhnacademy.byeol23front.orderset.order.client.OrderApiClient;
import com.nhnacademy.byeol23front.orderset.order.dto.OrderPrepareRequest;
import com.nhnacademy.byeol23front.orderset.order.dto.OrderPrepareResponse;
import com.nhnacademy.byeol23front.orderset.order.dto.PointOrderResponse;
import com.nhnacademy.byeol23front.orderset.order.exception.OrderPrepareFailException;

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
	private final OrderUtil orderUtil;

	@Value("${tossPayment.client-key}")
	private String tossClientKey;

	@PostMapping("/direct")
	@ResponseBody
	public ResponseEntity<Void> handleDirectOrder(@RequestBody BookOrderRequest request,
		@CookieValue(name = "Access-Token", required = false) String accessToken,
		HttpSession session) {

		if (Objects.isNull(accessToken) || accessToken.isEmpty()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		// 현재 액세스 토큰이 유효한지 검사하는 로직 jwtParser.jwtParserMemberId(accessToken) 이용 필요한지 확인할 필요가 있음
		session.setAttribute("directOrderRequest", request);

		return ResponseEntity.ok().build();
	}

	@GetMapping("/direct")
	public String getOrderFormDirect(HttpSession session, Model model) {
		BookOrderRequest request = (BookOrderRequest)session.getAttribute("directOrderRequest");

		if (Objects.isNull(request)) {
			return "redirect:/";
		}

		session.removeAttribute("directOrderRequest");

		orderUtil.addTotalQuantity(model, request.bookList());
		orderUtil.addDeliveryDatesToModel(model);
		orderUtil.addOrderSummary(model, request.bookList());
		orderUtil.addDeliveryFeeToModel(model, request);
		orderUtil.addPackagingOption(model);

		model.addAttribute("clientKey", tossClientKey);
		model.addAttribute("userPoint", 300_000);

		return "order/checkout";
	}

	@GetMapping
	public String getOrder(Model model) {
		orderUtil.addDeliveryDatesToModel(model);

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
	public ResponseEntity<OrderPrepareResponse> prepareOrder(@RequestBody OrderPrepareRequest request, @CookieValue(name = "Access-Token", required = false) String accessToken) {
		ResponseEntity<OrderPrepareResponse> response = orderApiClient.prepareOrder(request, accessToken);

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


}
