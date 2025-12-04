package com.nhnacademy.byeol23front.memberset.member.controller;

import java.time.Duration;
import java.util.UUID;

import com.nhnacademy.byeol23front.auth.CookieProperties;
import com.nhnacademy.byeol23front.commons.exception.DecodingFailureException;
import com.nhnacademy.byeol23front.memberset.domain.AccessToken;
import com.nhnacademy.byeol23front.memberset.domain.RefreshToken;
import com.nhnacademy.byeol23front.memberset.domain.Token;
import com.nhnacademy.byeol23front.memberset.member.client.MemberApiClient;
import com.nhnacademy.byeol23front.memberset.member.dto.LoginResponse;
import com.nhnacademy.byeol23front.memberset.member.dto.MemberRegisterRequest;
import com.nhnacademy.byeol23front.memberset.member.dto.PaycoTokenResponse;
import com.nhnacademy.byeol23front.memberset.member.dto.PaycoUserInfo;
import com.nhnacademy.byeol23front.memberset.member.dto.SocialLoginRequest;
import com.nhnacademy.byeol23front.memberset.member.service.MemberService;
import com.nhnacademy.byeol23front.memberset.member.service.PaycoOAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
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

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/payco")
@Tag(name = "OAuth", description = "페이코(Payco) 소셜 로그인 연동 API")
public class OAuthController {
	private final PaycoOAuthService paycoOAuthService;
	private final MemberApiClient memberApiClient;
	private final MemberService memberService;
	private final CookieProperties cookieProperties;

	@Value("${jwt.refresh-cookie.expiration}")
	private Long refreshCookieExp;

	@GetMapping("/login")
	@Operation(summary = "페이코 로그인 페이지로 리다이렉트",
		description = "CSRF 방지를 위한 STATE 쿠키를 설정하고, 페이코 인증 페이지로 리다이렉트합니다.")
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
	@Operation(summary = "페이코 콜백 처리",
		description = "페이코에서 전달된 인증 코드를 사용해 토큰을 발급받고, 사용자 정보를 조회합니다. " +
			"이미 가입된 사용자는 소셜 로그인으로 처리하고, 미가입자는 회원가입 페이지로 리다이렉트합니다.")
	public String paycoCallback(
		@RequestParam String code,
		RedirectAttributes redirectAttributes,
		HttpServletResponse response
	) {

		// 2. Access Token 발급
		PaycoTokenResponse tokenRes = paycoOAuthService.issueTokenFromPayco(code);
		String paycoAccessToken = tokenRes.accessToken();

		// 3. 사용자 정보 조회
		PaycoUserInfo userInfo = paycoOAuthService.getUserInfo(paycoAccessToken);

		if(memberService.findLoginId(userInfo.paycoId()).isDuplicated()) {
			SocialLoginRequest request = new SocialLoginRequest(userInfo.paycoId());
			LoginResponse loginResponse = memberApiClient.socialLogin(request);

			ResponseCookie refreshCookie = createCookie(new RefreshToken(loginResponse.refreshToken()));
			ResponseCookie accessCookie = createCookie(new AccessToken(loginResponse.accessToken()));

			//쿠키 적용
			response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
			response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());

			return "redirect:/";
		}

		redirectAttributes.addFlashAttribute("userInfo", userInfo);

		return "redirect:register";
	}

	@GetMapping("/register")
	@Operation(summary = "페이코 회원가입 폼",
		description = "페이코에서 받아온 사용자 정보를 기반으로 회원가입 폼을 초기화합니다.")
	public String paycoRegister(
		@ModelAttribute("form")MemberRegisterRequest request,
		@ModelAttribute("userInfo") PaycoUserInfo userInfo) {
		String rawPassword = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
		request.setLoginId(userInfo.paycoId());
		request.setLoginPassword(rawPassword);		//아무거나
		return "member/payco-register";
	}

	private ResponseCookie createCookie(Token token) {
		String path;
		String tokenType;
		Long expiration;			//중요: https요청에는 none으로 설정해도 됨, http요청은 secure가 false상태이므로 브라우저에서 none에 대한 쿠키는 거부하여 lax로 설정
		if(token instanceof RefreshToken) {
			tokenType = "Refresh-Token";
			expiration = refreshCookieExp;
			path = "/";
		} else if (token instanceof AccessToken) {
			tokenType = "Access-Token";
			expiration = -1L;		//session 방식
			path = "/";
		} else {
			throw new DecodingFailureException("토큰 에러");
		}
		return ResponseCookie.from(tokenType, token.getValue())
			.httpOnly(true)					//XSS
			.secure(cookieProperties.isSecure())					//중요: 실제 배포 환경에선 https요청으로 변경 / secure -> true
			.sameSite(cookieProperties.getSameSite())				//CSRF
			.path(path)
			.maxAge(Duration.ofMinutes(expiration))
			.build();
	}
}
