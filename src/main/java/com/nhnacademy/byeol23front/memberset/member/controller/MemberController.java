package com.nhnacademy.byeol23front.memberset.member.controller;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

import com.nhnacademy.byeol23front.commons.exception.DecodingFailureException;
import com.nhnacademy.byeol23front.memberset.domain.AccessToken;
import com.nhnacademy.byeol23front.memberset.domain.RefreshToken;
import com.nhnacademy.byeol23front.memberset.domain.Token;
import com.nhnacademy.byeol23front.memberset.member.dto.*;
import org.springframework.beans.factory.annotation.Value;
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

	@Value("${jwt.access-token.expiration}")
	private Long accessTokenExp;
	@Value("${jwt.refresh-token.expiration}")
	private Long refreshTokenExp;


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

		ResponseCookie refreshCookie = createCookie(new RefreshToken(loginResponse.refreshToken()));
		ResponseCookie accessCookie = createCookie(new AccessToken(loginResponse.accessToken()));

		//쿠키 적용
		response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
		response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());

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
		return memberService.findLoginId(loginId);
	}

	@PostMapping("/check-duplication")
	@ResponseBody
	public ValueDuplicationCheckResponse checkDuplication(
		@RequestBody ValueDuplicationCheckRequest request) {
		 return memberService.checkDuplication(request);
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

	@PostMapping("/put")
	@ResponseBody
	public ResponseEntity<MemberUpdateResponse> updateMember(@RequestBody MemberUpdateRequest request){
		MemberUpdateResponse response = memberService.updateMember(request);
		return ResponseEntity.ok().body(response);
	}

	@PostMapping("/put/password")
	@ResponseBody
	public ResponseEntity<MemberPasswordUpdateResponse> updatePassword(@RequestBody MemberPasswordUpdateRequest request){
		MemberPasswordUpdateResponse response = memberService.updateMemberPassword(request);
		return ResponseEntity.ok().body(response);
	}

	@PostMapping("/delete")
	@ResponseBody
	public ResponseEntity<Void> deleteMember() {
		memberService.deleteMember();
		return ResponseEntity.noContent().build();
	}


	private ResponseCookie createCookie(Token token) {
		String path;
		String tokenType;
		Long expiration;
		String prefix;
		String sameSite = "Lax";			//중요: https요청에는 none으로 설정해도 됨, http요청은 secure가 false상태이므로 브라우저에서 none에 대한 쿠키는 거부하여 lax로 설정
		if(token instanceof RefreshToken) {
			tokenType = "Refresh-Token";
			expiration = refreshTokenExp;
			path = "/";
		} else if (token instanceof AccessToken) {
			tokenType = "Access-Token";
			expiration = accessTokenExp;
			path = "/";
		} else {
			throw new DecodingFailureException("토큰 에러");
		}
		return ResponseCookie.from(tokenType, token.getValue())
				.httpOnly(true)					//XSS
				.secure(false)					//중요: 실제 배포 환경에선 https요청으로 변경 / secure -> true
				.sameSite(sameSite)				//CSRF
				.path(path)
				.maxAge(Duration.ofMinutes(expiration))
				.build();
	}
}
