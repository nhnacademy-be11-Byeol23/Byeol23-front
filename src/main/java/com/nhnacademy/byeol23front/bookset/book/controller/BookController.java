package com.nhnacademy.byeol23front.bookset.book.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.HttpHeaders;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
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

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
	private final BookApiClient bookApiClient;
	private final DeliveryApiClient deliveryApiClient;

	@GetMapping("/{book-id}")
	public String getBookById(@PathVariable(name = "book-id") Long bookId, Model model, HttpServletResponse response) {
        ResponseEntity<BookResponse> book = bookApiClient.getBook(bookId);
        List<String> cookies = book.getHeaders().get(HttpHeaders.SET_COOKIE);
        if(cookies != null) {
            cookies.stream().filter(StringUtils::isNotBlank).forEach(cookie -> {
                log.info("cookie: {}", cookie);
                response.addHeader(HttpHeaders.SET_COOKIE, cookie);
            });
        }

        DeliveryPolicyInfoResponse currentDeliveryPolicy = deliveryApiClient.getCurrentDeliveryPolicy().getBody();

		model.addAttribute("book", book.getBody());
		model.addAttribute("delivery", currentDeliveryPolicy);

		return "book/book-details";
	}
}
