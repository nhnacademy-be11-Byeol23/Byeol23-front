package com.nhnacademy.byeol23front.bookset.book.client;

import com.nhnacademy.byeol23front.bookset.book.dto.BookCreateRequest;
import com.nhnacademy.byeol23front.bookset.book.dto.BookOrderRequest;
import com.nhnacademy.byeol23front.bookset.book.dto.BookResponse;
import com.nhnacademy.byeol23front.bookset.book.dto.BookStockResponse;
import com.nhnacademy.byeol23front.bookset.book.dto.BookStockUpdateRequest;
import com.nhnacademy.byeol23front.bookset.book.dto.BookUpdateRequest;
import com.nhnacademy.byeol23front.bookset.tag.dto.PageResponse;
import com.nhnacademy.byeol23front.cartset.cart.dto.CartOrderRequest;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(
	name = "BYEOL23-GATEWAY",
	contextId = "bookApiClient"
)
public interface BookApiClient {
	@PostMapping("/api/books")
	BookResponse createBook(@RequestBody BookCreateRequest request);

	@GetMapping("/api/books/{book-id}")
    ResponseEntity<BookResponse> getBook(@PathVariable("book-id") Long bookId);

	@PutMapping("/api/books/{book-id}")
	BookResponse updateBook(@PathVariable("book-id") Long bookId,
		@RequestBody BookUpdateRequest request);

	@GetMapping("/api/books")
	ResponseEntity<PageResponse<BookResponse>> getBooks(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "20") int size
	);

	@GetMapping("/api/books/{book-id}/stock")
	BookStockResponse getBookStock(@PathVariable("book-id") Long bookId);

	@PutMapping("/api/books/{book-id}/stock")
	void updateBookStock(@PathVariable("book-id") Long bookId, @RequestBody BookStockUpdateRequest request);

	@DeleteMapping("/api/books/{book-id}")
	void deleteBook(@PathVariable("book-id") Long bookId);

	@GetMapping("/api/books/list")
	List<BookResponse> getBooksByIds(@RequestParam("ids") List<Long> bookIds);

	@PostMapping("/api/books/orders")
	ResponseEntity<BookOrderRequest> getBookOrder(@RequestBody CartOrderRequest cartOrderRequest);

}
