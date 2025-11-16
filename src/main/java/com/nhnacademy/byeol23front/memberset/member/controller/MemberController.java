package com.nhnacademy.byeol23front.memberset.member.controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.ResponseBody;

import com.nhnacademy.byeol23front.memberset.member.client.MemberApiClient;
import com.nhnacademy.byeol23front.memberset.member.dto.LoginRequest;
import com.nhnacademy.byeol23front.memberset.member.dto.LoginResponse;
import com.nhnacademy.byeol23front.memberset.member.dto.MemberRegisterRequest;
import com.nhnacademy.byeol23front.memberset.member.dto.MyPageResponse;

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
	public String showLoginForm() {
		return "member/login";
	}


	@PostMapping("/login")
	public String login(@ModelAttribute LoginRequest request, HttpServletResponse response) {
		ResponseEntity<LoginResponse> feignResponse = memberApiClient.login(request);
		List<String> setCookies = feignResponse.getHeaders().get(HttpHeaders.SET_COOKIE);

		if (setCookies != null) {
			setCookies.forEach(c -> response.addHeader(HttpHeaders.SET_COOKIE, c));
			setCookies.forEach(c -> log.info("Upstream Set-Cookie: {}", c));
		}
		return "redirect:/";
	}

	@PostMapping("/logout")
	public String logout(@ModelAttribute LogoutRequest request, HttpServletResponse response) {
		ResponseEntity<LogoutResponse> feignResponse = memberApiClient.logout();

		response.addHeader("Set-Cookie", deleteCookie("Access-Token", "/"));
		response.addHeader("Set-Cookie", deleteCookie("Refresh-Token", "/members"));

		return "redirect:/members/login";
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

	@GetMapping("/mypage")
	public String getMypage(Model model){
		//TODO: 차후 삭제
		//MyPageResponse resp = memberApiClient.getMember().getBody();
		//model.addAttribute("member", resp);
		return "/member/mypage";
	}

	@PutMapping("/mypage")
	@ResponseBody
	public ResponseEntity<MemberUpdateResponse> updateMember(@RequestBody MemberUpdateRequest request){
		return memberApiClient.updateMember(request);
	}

	@PutMapping("/mypage/password")
	@ResponseBody
	public ResponseEntity<MemberPasswordUpdateResponse> updateMemberPassword(@RequestBody MemberPasswordUpdateRequest request){
		return memberApiClient.updateMemberPassword(request);
	}

	@PutMapping("/mypage/reactivate")
	@ResponseBody
	public ResponseEntity<Void> reactivateMember(@RequestBody MemberPasswordUpdateRequest request){
		return memberApiClient.reactivateMember(request);
	}

	@DeleteMapping("/mypage")
	@ResponseBody
	public ResponseEntity<Void> deleteMember(){
		return memberApiClient.deleteMember();
	}
}
