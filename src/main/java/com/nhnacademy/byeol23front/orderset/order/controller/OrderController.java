package com.nhnacademy.byeol23front.orderset.order.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.nhnacademy.byeol23front.bookset.category.dto.CategoryLeafResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nhnacademy.byeol23front.bookset.book.client.BookApiClient;
import com.nhnacademy.byeol23front.bookset.book.dto.BookInfoRequest;
import com.nhnacademy.byeol23front.bookset.book.dto.BookOrderRequest;
import com.nhnacademy.byeol23front.bookset.book.dto.BookResponse;
import com.nhnacademy.byeol23front.memberset.member.client.MemberApiClient;
import com.nhnacademy.byeol23front.memberset.member.dto.MemberMyPageResponse;
import com.nhnacademy.byeol23front.orderset.delivery.client.DeliveryApiClient;
import com.nhnacademy.byeol23front.orderset.order.client.OrderApiClient;
import com.nhnacademy.byeol23front.orderset.order.dto.OrderCancelRequest;
import com.nhnacademy.byeol23front.orderset.order.dto.OrderCancelResponse;
import com.nhnacademy.byeol23front.orderset.order.dto.OrderPrepareRequest;
import com.nhnacademy.byeol23front.orderset.order.dto.OrderPrepareResponse;
import com.nhnacademy.byeol23front.orderset.order.dto.PointOrderResponse;
import com.nhnacademy.byeol23front.orderset.order.exception.OrderPrepareFailException;

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
	private final BookApiClient bookApiClient;
	private final MemberApiClient memberApiClient;

	@Value("${tossPayment.client-key}")
	private String tossClientKey;

	@PostMapping("/direct")
	@ResponseBody
	public ResponseEntity<Void> handleDirectOrder(@CookieValue(name = "Access-Token", required = false) String token) {

		if (Objects.isNull(token)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@GetMapping("/direct")
	public String getOrderFormDirect(@RequestParam Long bookId, @RequestParam int quantity, Model model) {

		BookResponse book = bookApiClient.getBook(bookId).getBody();
		MemberMyPageResponse member = memberApiClient.getMember().getBody();

		String firstImageUrl = (book.images() != null && !book.images().isEmpty())
			? book.images().getFirst().imageUrl()
			: "https://image.yes24.com/momo/Noimg_L.jpg";

		List<BookInfoRequest> bookOrderInfo = List.of(new BookInfoRequest(bookId, book.bookName(),
			firstImageUrl, book.isPack(), book.regularPrice(), book.salePrice(), book.publisher(), quantity,
			book.contributors(),
			null));

		BookOrderRequest request = new BookOrderRequest(bookOrderInfo);

		orderUtil.addTotalQuantity(model, request.bookList());
		orderUtil.addDeliveryDatesToModel(model);
		orderUtil.addOrderSummary(model, request.bookList());
		orderUtil.addDeliveryFeeToModel(model, request);
		orderUtil.addPackagingOption(model);

		model.addAttribute("userPoint", member.currentPoint());

		model.addAttribute("clientKey", tossClientKey);

		return "order/checkout";
	}


	@PostMapping("/prepare")
	@ResponseBody
	public ResponseEntity<OrderPrepareResponse> prepareOrder(@RequestBody OrderPrepareRequest request,
		@CookieValue(name = "Access-Token", required = false) String accessToken) {
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

	@PostMapping("/{order-number}/cancel")
	@ResponseBody
	public ResponseEntity<OrderCancelResponse> cancelOrder(@PathVariable(name = "order-number") String orderNumber,
		@RequestBody OrderCancelRequest request) {

		OrderCancelResponse response = orderApiClient.cancelOrder(orderNumber, request).getBody();

		return ResponseEntity.ok(response);
	}

}
