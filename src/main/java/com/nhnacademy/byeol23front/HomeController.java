package com.nhnacademy.byeol23front;

import java.util.List;

import com.nhnacademy.byeol23front.bookset.search.client.SearchApiClient;
import com.nhnacademy.byeol23front.bookset.search.dto.BookSearchResultLongIdResponse;
import com.nhnacademy.byeol23front.bookset.search.dto.BookSearchResultResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequiredArgsConstructor
public class HomeController {
	private static final Logger log = LoggerFactory.getLogger(HomeController.class);
	private final SearchApiClient searchApiClient;

	@GetMapping("/")
	public String mainPage(Model model) {
		var top3BooksResult = searchApiClient.searchBestBooks(0, 3);
		var top3Books = BookSearchResultLongIdResponse.fromList(top3BooksResult.content());

		var bestBooksResult = searchApiClient.searchBestBooks(0, 12);
		var bestBooks = BookSearchResultLongIdResponse.fromList(bestBooksResult.content());
		
		log.info("=== 메인 페이지 베스트 도서 조회 ===");
		log.info("Hero Section 도서 수: {}", top3Books.size());
		log.info("Best Sellers Section 도서 수: {}", bestBooks.size());
		
		model.addAttribute("top3Books", top3Books);
		model.addAttribute("bestBooks", bestBooks);
		return "index";
	}

	@GetMapping("/error")
	public String showError() {
		return "error";
	}



}
