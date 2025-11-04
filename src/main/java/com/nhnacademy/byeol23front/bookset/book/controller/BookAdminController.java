package com.nhnacademy.byeol23front.bookset.book.controller;

import com.nhnacademy.byeol23front.bookset.book.client.BookApiClient;
import com.nhnacademy.byeol23front.bookset.book.dto.BookCreateRequest;
import com.nhnacademy.byeol23front.bookset.book.dto.BookUpdateRequest;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/books")
@RequiredArgsConstructor
public class BookAdminController {
	private final BookApiClient bookApiClient;

	@PostMapping("/new")
	@ResponseBody
	public ResponseEntity<Void> createBook(@RequestBody BookCreateRequest request) {
		try {
			bookApiClient.createBook(request);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.internalServerError().build();
		}
	}

	@PutMapping("/{book-id}")
	@ResponseBody
	public ResponseEntity<Void> updateBook(@PathVariable("book-id") Long id, @RequestBody BookUpdateRequest request){
		try {
			bookApiClient.updateBook(id, request);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.internalServerError().build();
		}
	}

	@GetMapping("/update/{book-id}")
	public String bookUpdateForm(@PathVariable("book-id") Long id, Model model){
		model.addAttribute("book", bookApiClient.getBook(id));
		return "admin/book/bookUpdateForm";
	}

	@GetMapping
	public String getBooks(Model model){
		model.addAttribute("books", bookApiClient.getBooks());
		return "admin/book/bookList";
	}

	@GetMapping("/new")
	public String bookForm() {
		return "admin/book/bookForm";
	}

	@DeleteMapping("/{book-id}")
	@ResponseBody
	public ResponseEntity<Void> deleteBook(@PathVariable("book-id") Long id) {
		try {
			bookApiClient.deleteBook(id);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.internalServerError().build();
		}
	}
}
