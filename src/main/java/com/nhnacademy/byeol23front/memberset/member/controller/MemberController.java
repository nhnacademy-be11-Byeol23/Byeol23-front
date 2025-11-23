package com.nhnacademy.byeol23front.memberset.member.controller;

import java.util.List;
import java.util.Objects;

import com.nhnacademy.byeol23front.memberset.member.dto.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.nhnacademy.byeol23front.memberset.member.client.MemberApiClient;

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
		if (br.hasErrors()) {
			return "member/register";
		}
		memberApiClient.registerRequest(request);
		return "member/login";
	}

	@GetMapping("/login")
	public String showLoginForm(@RequestParam(name = "bookId", required = false) Long bookId,
		@RequestParam(name = "quantity", required = false) Integer quantity,
		Model model) {

		if (!Objects.isNull(bookId) && !Objects.isNull(quantity)) {
			model.addAttribute("bookId", bookId);
			model.addAttribute("quantity", quantity);
		}

		return "member/login";
	}


	@PostMapping("/login")
	public String login(@ModelAttribute LoginRequestTmp tmp, HttpServletResponse response) {
		LoginRequest request = new LoginRequest(tmp.getLoginId(), tmp.getLoginPassword());

		ResponseEntity<LoginResponse> feignResponse = memberApiClient.login(request);
		List<String> setCookies = feignResponse.getHeaders().get(HttpHeaders.SET_COOKIE);

		if (setCookies != null) {
			setCookies.forEach(c -> response.addHeader(HttpHeaders.SET_COOKIE, c));
			setCookies.forEach(c -> log.info("Upstream Set-Cookie: {}", c));
		}

		if (!Objects.isNull(tmp.getBookId()) && !Objects.isNull(tmp.getQuantity())) {
			return String.format("redirect:/orders/direct?bookId=%d&quantity=%d",
				tmp.getBookId(), tmp.getQuantity());
		}

		return "redirect:/";
	}

	@PostMapping("/logout")
	public String logout(@ModelAttribute LogoutRequest request, HttpServletResponse response) {
		ResponseEntity<LogoutResponse> feignResponse = memberApiClient.logout();

		response.addHeader("Set-Cookie", deleteCookie("Access-Token", "/"));
		response.addHeader("Set-Cookie", deleteCookie("Refresh-Token", "/members"));

		return "redirect:/";
	}

	private String deleteCookie(String name, String path) {
		return ResponseCookie.from(name, "")
				.path(path)
				.httpOnly(true)
				.secure(false)
				.sameSite("Lax")
				.maxAge(0)
				.build().toString();
	}

}
