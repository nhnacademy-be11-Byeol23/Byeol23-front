package com.nhnacademy.byeol23front;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
@Slf4j
@RequiredArgsConstructor
public class AdminController {


    @GetMapping("/")
    public String mainPage() {
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
