package com.nhnacademy.byeol23front.bookset.category.controller;

import com.nhnacademy.byeol23front.bookset.category.client.CategoryApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class CategoryViewController {
    private final CategoryApiClient categoryApiClient;

    @GetMapping("/categories")
    public String categoryPage(Model model) {
        model.addAttribute("pageTitle", "카테고리 관리");
        model.addAttribute("categoryCss", "admin/category/category-css :: categoryCss");
        model.addAttribute("categoryJs", "admin/category/category-js :: categoryJs");
        model.addAttribute("categories", categoryApiClient.getRoots());
        return "admin/management";
    }
}
