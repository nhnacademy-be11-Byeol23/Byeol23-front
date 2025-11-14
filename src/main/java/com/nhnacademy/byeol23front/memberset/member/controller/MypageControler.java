package com.nhnacademy.byeol23front.memberset.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MypageControler {
    @GetMapping
    public String getMypage() {
        return "/account";
    }
}
