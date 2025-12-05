package com.nhnacademy.byeol23front.orderset.order.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.nhnacademy.byeol23front.bookset.book.client.BookApiClient;
import com.nhnacademy.byeol23front.bookset.book.dto.BookInfoRequest;
import com.nhnacademy.byeol23front.bookset.book.dto.BookOrderRequest;
import com.nhnacademy.byeol23front.bookset.book.dto.BookResponse;
import com.nhnacademy.byeol23front.orderset.order.dto.OrderRequest;
import com.nhnacademy.byeol23front.memberset.member.dto.NonmemberOrderRequest;
import com.nhnacademy.byeol23front.orderset.order.client.OrderApiClient;
import com.nhnacademy.byeol23front.orderset.order.dto.OrderDetailResponse;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/orders/nonmember")
@RequiredArgsConstructor
public class NonmemberOrderController {
	private final BookApiClient bookApiClient;
	private final OrderUtil orderUtil;
	private final OrderApiClient orderApiClient;

	@Value("${tossPayment.client-key}")
	private String tossClientKey;

	@GetMapping
	public String getNonmemberOrderForm(@RequestParam("token") String token,
		Model model) {

		OrderRequest orderRequest = orderApiClient.getAndRemoveOrderRequest(token);
		BookOrderRequest bookOrderRequest = bookApiClient.getBookOrder(orderRequest).getBody();

		orderUtil.addDeliveryDatesToModel(model);
		orderUtil.addDeliveryFeeToModel(model, bookOrderRequest);
		orderUtil.addOrderSummary(model, bookOrderRequest);
		orderUtil.addTotalQuantity(model, bookOrderRequest.bookList());
		orderUtil.addPackagingOption(model);

		model.addAttribute("clientKey", tossClientKey);

		return "order/nonmemberCheckout";
	}

	@GetMapping("/direct")
	public String getNonMemberDirectOrderForm(@RequestParam Long bookId,
		@RequestParam int quantity,
		Model model) {

		BookResponse book = bookApiClient.getBook(bookId).getBody();

		BookInfoRequest bookInfo = new BookInfoRequest(
			bookId, book.bookName(), book.images().getFirst().imageUrl(), // 이미지 null 체크는 Service에서 처리해야 안전함
			book.isPack(), book.regularPrice(), book.salePrice(), book.publisher(), quantity,
			book.contributors(), null
		);
		List<BookInfoRequest> bookOrderInfo = List.of(bookInfo);
		BookOrderRequest request = new BookOrderRequest(bookOrderInfo); // BookOrderRequest DTO 사용

		orderUtil.addDeliveryDatesToModel(model);
		orderUtil.addDeliveryFeeToModel(model, request);
		orderUtil.addOrderSummary(model, request);
		orderUtil.addTotalQuantity(model, request.bookList());
		orderUtil.addPackagingOption(model);

		model.addAttribute("clientKey", tossClientKey);

		return "order/nonmemberCheckout";

	}

	@GetMapping("/lookup")
	public String lookUpPage() {
		return "order/nonmemberOrderLookUp";
	}

	@PostMapping("/detail")
	public String nonmemberOrderDetail(@ModelAttribute NonmemberOrderRequest request, Model model) {
		OrderDetailResponse orderDetail = orderApiClient.getNonmemberOrder(request);

		model.addAttribute("order", orderDetail);
		orderUtil.addFinalPaymentAmountToModel(model, orderDetail);

		return "order/nonmemberOrderDetail";
	}

}
