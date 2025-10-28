package com.nhnacademy.byeol23front.bookset.category.controller;

import java.util.List;

import com.nhnacademy.byeol23front.bookset.category.client.CategoryApiClient;
import com.nhnacademy.byeol23front.orderset.order.client.OrderApiClient;
import com.nhnacademy.byeol23front.orderset.order.dto.OrderInfoResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class CategoryViewController {
    private final CategoryApiClient categoryApiClient;

    @GetMapping("/categories")
    public String categoryPage(Model model) {
        model.addAttribute("pageTitle", "카테고리 관리");
        model.addAttribute("categories", categoryApiClient.getRoots());

        return "admin/category/category";
    }

}
