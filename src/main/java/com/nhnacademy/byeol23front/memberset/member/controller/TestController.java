package com.nhnacademy.byeol23front.memberset.member.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import com.nhnacademy.byeol23front.memberset.JwtParser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class TestController {

	private final JwtParser jwtParser;

	@GetMapping("/test")
	public String test(@CookieValue(name = "Access-Token") String refreshToken) {
		log.info("리프레시 토큰: {}", refreshToken);

		log.info("멤버 아이디: {} ", jwtParser.jwtParseMemberId(refreshToken));

		return "redirect:/";
	}
}
