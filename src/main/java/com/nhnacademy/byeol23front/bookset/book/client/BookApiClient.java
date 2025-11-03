package com.nhnacademy.byeol23front.bookset.book.client;

import com.nhnacademy.byeol23front.bookset.book.dto.BookCreateRequest;
import com.nhnacademy.byeol23front.bookset.book.dto.BookResponse;
import com.nhnacademy.byeol23front.bookset.book.dto.BookUpdateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(
	name = "bookApiClient",
	url = "${backend.api.url}"
)
public interface BookApiClient {
	@PostMapping("/api/books")
	BookResponse createBook(@RequestBody BookCreateRequest request);

	@GetMapping("/api/books/{book-id}")
	BookResponse getBook(@PathVariable("book-id") Long bookId);

	@PutMapping("/api/books/{book-id}")
	BookResponse updateBook(@PathVariable("book-id") Long bookId,
		@RequestBody BookUpdateRequest request);

	@GetMapping("/api/books")
	List<BookResponse> getBooks();

	@DeleteMapping("/api/books/{book-id}")
	void deleteBook(@PathVariable("book-id") Long bookId);
}
