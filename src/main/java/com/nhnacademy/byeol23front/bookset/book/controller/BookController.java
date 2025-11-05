package com.nhnacademy.byeol23front.bookset.book.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.nhnacademy.byeol23front.bookset.book.client.BookApiClient;
import com.nhnacademy.byeol23front.bookset.book.dto.BookResponse;
import com.nhnacademy.byeol23front.orderset.delivery.client.DeliveryApiClient;
import com.nhnacademy.byeol23front.orderset.delivery.dto.DeliveryPolicyInfoResponse;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
	private final BookApiClient bookApiClient;
	private final DeliveryApiClient deliveryApiClient;

	@GetMapping("/{book-id}")
	public String getBookById(@PathVariable(name = "book-id") Long bookId, Model model) {
		BookResponse bookDetail = bookApiClient.getBook(bookId);

		DeliveryPolicyInfoResponse currentDeliveryPolicy = deliveryApiClient.getCurrentDeliveryPolicy().getBody();

		model.addAttribute("book", bookDetail);
		model.addAttribute("delivery", currentDeliveryPolicy);

		return "book/book-details";
	}



}
