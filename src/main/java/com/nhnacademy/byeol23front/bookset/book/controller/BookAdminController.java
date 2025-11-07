package com.nhnacademy.byeol23front.bookset.book.controller;

import com.nhnacademy.byeol23front.bookset.book.client.BookApiClient;
import com.nhnacademy.byeol23front.bookset.book.dto.BookCreateRequest;
import com.nhnacademy.byeol23front.bookset.book.dto.BookUpdateRequest;
import com.nhnacademy.byeol23front.bookset.category.client.CategoryApiClient;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.nhnacademy.byeol23front.bookset.book.dto.BookResponse;
import com.nhnacademy.byeol23front.bookset.category.dto.CategoryLeafResponse;

import java.util.List;

@Controller
@RequestMapping("/admin/books")
@RequiredArgsConstructor
public class BookAdminController {
	private final BookApiClient bookApiClient;
	private final CategoryApiClient categoryApiClient;

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
		try {
			BookResponse book = bookApiClient.getBook(id);
			model.addAttribute("book", book);
			model.addAttribute("categories", categoryApiClient.getRoots());

			// 선택된 카테고리 ID 리스트 계산
			List<Long> selectedCategoryIds = book.categories() != null
				? book.categories().stream().map(CategoryLeafResponse::id).toList()
				: java.util.Collections.emptyList();
			model.addAttribute("selectedCategoryIds", selectedCategoryIds);

			return "admin/book/bookUpdateForm";
		} catch (Exception e) {
			// 에러 처리
			e.printStackTrace();
			model.addAttribute("categories", java.util.Collections.emptyList());
			model.addAttribute("selectedCategoryIds", java.util.Collections.emptyList());
			return "admin/book/bookUpdateForm";
		}
	}

	@GetMapping
	public String getBooks(Model model){
		model.addAttribute("books", bookApiClient.getBooks());
		return "admin/book/bookList";
	}

	@GetMapping("/new")
	public String bookForm(Model model) {
		model.addAttribute("categories", categoryApiClient.getRoots());
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
