package com.nhnacademy.byeol23front.memberset.member.controller;

import java.time.Duration;
import java.util.UUID;

import com.nhnacademy.byeol23front.memberset.member.service.MemberService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.nhnacademy.byeol23front.memberset.member.client.MemberApiClient;
import com.nhnacademy.byeol23front.memberset.member.dto.LoginRequest;
import com.nhnacademy.byeol23front.memberset.member.dto.MemberRegisterRequest;
import com.nhnacademy.byeol23front.memberset.member.dto.PaycoTokenResponse;
import com.nhnacademy.byeol23front.memberset.member.dto.PaycoUserInfo;
import com.nhnacademy.byeol23front.memberset.member.dto.SocialLoginRequest;
import com.nhnacademy.byeol23front.memberset.member.service.PaycoOAuthService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/payco")
public class OAuthController {
	private final PaycoOAuthService paycoOAuthService;
	private final MemberApiClient memberApiClient;
	private final MemberService memberService;

	@GetMapping("/login")
	public ResponseEntity<Void> redirectToPayco(HttpServletResponse servletResponse) {
		String state = paycoOAuthService.generateState();

		//CSRF 방지용
		ResponseCookie stateCookie = ResponseCookie.from("PAYCO_STATE", state)
			.httpOnly(true)
			.secure(false)
			.path("/")
			.maxAge(Duration.ofMinutes(10))
			.build();

		servletResponse.addHeader(HttpHeaders.SET_COOKIE, stateCookie.toString());
		String authorizeUrl = paycoOAuthService.buildAuthorizeUrl(state);


		//HttpStatus.FOUND : 페이코로 리다이렉트
		// 헤더에 CSRF방지를 위한 STATECookie 추가
		return ResponseEntity.status(HttpStatus.FOUND)
			.header(HttpHeaders.LOCATION, authorizeUrl)
			.build();
	}


	// 페이코로부터 응답을 받은 URI
	// 2-4 과정 실행
	@GetMapping("/callback")
	public String paycoCallback(
		@RequestParam String code,
		RedirectAttributes redirectAttributes
	) {

		// 2. Access Token 발급
		PaycoTokenResponse tokenRes = paycoOAuthService.issueTokenFromPayco(code);
		String paycoAccessToken = tokenRes.accessToken();

		// 3. 사용자 정보 조회
		PaycoUserInfo userInfo = paycoOAuthService.getUserInfo(paycoAccessToken);

		if(memberService.findLoginId(userInfo.paycoId()).isDuplicated()) {
			SocialLoginRequest request = new SocialLoginRequest(userInfo.paycoId());
			memberApiClient.socialLogin(request);

			return "redirect:/";
		}

		redirectAttributes.addFlashAttribute("userInfo", userInfo);

		return "redirect:register";
	}

	@GetMapping("/register")
	public String paycoRegister(
		@ModelAttribute("form")MemberRegisterRequest request,
		@ModelAttribute("userInfo") PaycoUserInfo userInfo) {
		String rawPassword = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
		request.setLoginId(userInfo.paycoId());
		request.setLoginPassword(rawPassword);		//아무거나
		return "member/payco-register";
	}
}
