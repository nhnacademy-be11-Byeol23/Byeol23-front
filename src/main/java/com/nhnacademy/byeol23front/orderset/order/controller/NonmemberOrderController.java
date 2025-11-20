package com.nhnacademy.byeol23front.orderset.order.controller;

import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.nhnacademy.byeol23front.bookset.book.client.BookApiClient;
import com.nhnacademy.byeol23front.bookset.book.dto.BookInfoRequest;
import com.nhnacademy.byeol23front.bookset.book.dto.BookOrderRequest;
import com.nhnacademy.byeol23front.bookset.book.dto.BookResponse;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/orders/nonmember")
@RequiredArgsConstructor
public class NonmemberOrderController {
	private final BookApiClient bookApiClient;
	private final OrderUtil orderUtil;

	@Value("${tossPayment.client-key}")
	private String tossClientKey;

	@GetMapping("/direct")
	public String getOrderFormDirect(@RequestParam(name = "bookId") Long bookId,
		@RequestParam(name = "quantity") int quantity,
		Model model) {

		BookResponse book = bookApiClient.getBook(bookId).getBody();

		BookOrderRequest bookOrderRequest = getBookOrderRequest(quantity, book);

		model.addAttribute("orderItem", bookOrderRequest.bookList());
		model.addAttribute("quantity", quantity);
		model.addAttribute("tossClientKey", tossClientKey);

		orderUtil.addTotalQuantity(model, bookOrderRequest.bookList());
		orderUtil.addDeliveryDatesToModel(model);
		orderUtil.addOrderSummary(model, bookOrderRequest.bookList());
		orderUtil.addDeliveryFeeToModel(model, bookOrderRequest);
		orderUtil.addPackagingOption(model);
		
		return "order/nonmemberCheckout";
	}


	@NotNull
	private static BookOrderRequest getBookOrderRequest(int quantity, BookResponse book) {
		String firstImageUrl = "https://image.yes24.com/momo/Noimg_L.jpg"; // 기본 이미지
		if (book.images() != null && !book.images().isEmpty()) {
			firstImageUrl = book.images().getFirst().imageUrl();
		}

		List<BookInfoRequest> bookInfoRequest = Collections.singletonList(new BookInfoRequest(
			book.bookId(),
			book.bookName(),
			firstImageUrl,
			book.isPack(),
			book.regularPrice(),
			book.salePrice(),
			book.publisher(),
			quantity,
			book.contributors(),
			null
		));

		BookOrderRequest bookOrderRequest = new BookOrderRequest(bookInfoRequest);
		return bookOrderRequest;
	}

}
