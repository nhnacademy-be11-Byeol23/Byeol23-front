package com.nhnacademy.byeol23front;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.nhnacademy.byeol23front.bookset.category.client.CategoryApiClient;
import com.nhnacademy.byeol23front.orderset.order.client.OrderApiClient;
import com.nhnacademy.byeol23front.orderset.order.dto.OrderInfoResponse;

@Controller
@Slf4j
@RequiredArgsConstructor
public class TestController {
    private final OrderApiClient orderApiClient; // 주문 API 클라이언트
    private final CategoryApiClient categoryApiClient; // 카테고리 API 클라이언트 (기존 코드)


    @GetMapping("/")
    public String mainPage() {
        log.info("front");
        return "index";
    }

    @GetMapping("/error")
    public String showError() {
        return "error";
    }

    @GetMapping("/admin")
    public String adminMainPage(Model model) {
        model.addAttribute("pageTitle", "관리자 메인");

        return "admin/management";
    }
}
