package com.nhnacademy.byeol23front.bookset.bookAladin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nhnacademy.byeol23front.bookset.bookAladin.dto.AladinResult;
import com.nhnacademy.byeol23front.bookset.bookAladin.service.BookAladinService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/bookApi")
public class BookAladinController {

	private final BookAladinService bookAladinService;

	@GetMapping
	public String getAllBooks(@RequestParam(required = false) String keyword, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int size, Model model) throws JsonProcessingException {
		AladinResult result = bookAladinService.getAllBooks(keyword, page, size);
		model.addAttribute("books", result.item());
		model.addAttribute("page", result.page());
		model.addAttribute("size", result.size());
		model.addAttribute("lastPage", result.lastPage());
		model.addAttribute("keyword", result.keyword());
		model.addAttribute("total", result.total());

		return "/admin/bookAladin/bookAladin";
	}

	@GetMapping("/new")
	public String createBook(){
		return "/admin/bookAladin/bookAladinCreate";
	}

}
