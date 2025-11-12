package com.nhnacademy.byeol23front.memberset.member.controller;

import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.nhnacademy.byeol23front.memberset.member.client.MemberApiClient;
import com.nhnacademy.byeol23front.memberset.member.dto.LoginRequest;
import com.nhnacademy.byeol23front.memberset.member.dto.LoginResponse;
import com.nhnacademy.byeol23front.memberset.member.dto.MemberRegisterRequest;

import jakarta.servlet.http.HttpServletRequest;
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

	@PostMapping("/login")
	public ResponseEntity<?> login(
		@ModelAttribute LoginRequest request,
		BindingResult br,
		HttpServletRequest httpReq) {

		if (br.hasErrors()) {
			return ResponseEntity.badRequest().body("invalid request");
		}

		ResponseEntity<LoginResponse> upstream = memberApiClient.login(request);
		HttpHeaders headers = new HttpHeaders();
		String accessToken = upstream.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
		if (accessToken != null) {
			headers.add(HttpHeaders.AUTHORIZATION, accessToken);
		}

		var setCookies = upstream.getHeaders().get(HttpHeaders.SET_COOKIE);
		if (setCookies != null && !setCookies.isEmpty()) {
			headers.put(HttpHeaders.SET_COOKIE, setCookies);
		}
		log.info("accessToken {} , cookie {}", accessToken, setCookies);
		boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(httpReq.getHeader("X-Requested-With"));
		if (isAjax) {
			return ResponseEntity
				.status(upstream.getStatusCode())
				.headers(headers)
				.body(upstream.getBody());
		} else {
			headers.setLocation(URI.create("/"));
			return ResponseEntity.status(HttpStatus.FOUND)
				.headers(headers)
				.location(URI.create("/"))
				.build();
		}
	}
}
