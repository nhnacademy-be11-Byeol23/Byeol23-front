package com.nhnacademy.byeol23front.memberset.member.controller;

import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.nhnacademy.byeol23front.memberset.member.client.MemberApiClient;
import com.nhnacademy.byeol23front.memberset.member.dto.LoginRequest;
import com.nhnacademy.byeol23front.memberset.member.dto.LoginResponse;
import com.nhnacademy.byeol23front.memberset.member.dto.MemberRegisterRequest;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {
	private final MemberApiClient memberApiClient;

	@GetMapping("/register")
	public String showRegisterForm() {
		return "member/register";
	}

	@PostMapping("/register")
	public String register(@ModelAttribute MemberRegisterRequest request, BindingResult br) {
		if(br.hasErrors()) {
			return "member/register";
		}
		memberApiClient.registerRequest(request);
		return "member/login";
	}

	@GetMapping("/login")
	public String showLoginForm() { return "member/login"; }


	@PostMapping("/login")public String login(@ModelAttribute LoginRequest request, HttpServletResponse response) {
		ResponseEntity<LoginResponse> feignResponse = memberApiClient.login(request);
		List<String> setCookies = feignResponse.getHeaders().get(HttpHeaders.SET_COOKIE);
		if (setCookies != null) {
			setCookies.forEach(c -> response.addHeader(HttpHeaders.SET_COOKIE, c));
		}
		return "redirect:/";
	}

	@GetMapping("/test")
	public String showMyPage() {
		return "member/test";
	}
}
