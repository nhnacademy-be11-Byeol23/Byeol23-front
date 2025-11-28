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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nhnacademy.byeol23front.memberset.member.service.MemberService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {
	private final MemberService memberService;

	private final String accessCookieHeader = "access-token";
	private final String refreshCookieHeader = "refresh-token";

	@GetMapping("/register")
	public String showRegisterForm() {
		return "member/register";
	}

	@PostMapping("/register")
	public String register(@ModelAttribute MemberRegisterRequest request, BindingResult br) {
		log.info("request:{}", request);
		if (br.hasErrors()) {
			return "member/register";
		}
		memberService.register(request);
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
		LoginResponse loginResponse = memberService.login(request);




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
		memberService.logout();

		response.addHeader("Set-Cookie", deleteCookie("Access-Token", "/"));
		response.addHeader("Set-Cookie", deleteCookie("Refresh-Token", "/members"));

		return "redirect:/";
	}

	@GetMapping("/check-id")
	@ResponseBody
	public FindLoginIdResponse findLoginId(@RequestParam String loginId) {
		FindLoginIdResponse response = memberService.checkId();
		return response;
	}

	@PostMapping("/check-duplication")
	@ResponseBody
	public ValueDuplicationCheckResponse checkDuplication(
		@RequestBody ValueDuplicationCheckRequest request) {
		ValueDuplicationCheckResponse response = memberApiClient.checkDuplication(request);
		return response;
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

	@PutMapping
	@ResponseBody
	public ResponseEntity<MemberUpdateResponse> updateMember(@RequestBody MemberUpdateRequest req){
		return memberApiClient.updateMember(req);
	}

	@PutMapping("/password")
	@ResponseBody
	public ResponseEntity<MemberPasswordUpdateResponse> updatePassword(@RequestBody MemberPasswordUpdateRequest req){
		return memberApiClient.updateMemberPassword(req);
	}

	@DeleteMapping
	@ResponseBody
	public ResponseEntity<Void> deleteMember(){
		return memberApiClient.deleteMember();
	}


}
