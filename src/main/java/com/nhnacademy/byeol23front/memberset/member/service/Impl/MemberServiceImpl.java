package com.nhnacademy.byeol23front.memberset.member.service.Impl;

import java.lang.reflect.Member;
import java.time.Duration;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nhnacademy.byeol23front.commons.exception.DecodingFailureException;
import com.nhnacademy.byeol23front.memberset.domain.AccessToken;
import com.nhnacademy.byeol23front.memberset.domain.RefreshToken;
import com.nhnacademy.byeol23front.memberset.domain.Token;
import com.nhnacademy.byeol23front.memberset.member.client.MemberApiClient;
import com.nhnacademy.byeol23front.memberset.member.dto.FindLoginIdResponse;
import com.nhnacademy.byeol23front.memberset.member.dto.LoginRequest;
import com.nhnacademy.byeol23front.memberset.member.dto.LoginResponse;
import com.nhnacademy.byeol23front.memberset.member.dto.LogoutRequest;
import com.nhnacademy.byeol23front.memberset.member.dto.MemberRegisterRequest;
import com.nhnacademy.byeol23front.memberset.member.dto.MemberRegisterResponse;
import com.nhnacademy.byeol23front.memberset.member.dto.ValueDuplicationCheckRequest;
import com.nhnacademy.byeol23front.memberset.member.dto.ValueDuplicationCheckResponse;
import com.nhnacademy.byeol23front.memberset.member.service.MemberService;

import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

	private final MemberApiClient memberApiClient;

	@Override
	public void register(MemberRegisterRequest request) {
		memberApiClient.registerRequest(request);
	}

	@Override
	public LoginResponse login(LoginRequest request) {
		ResponseEntity<LoginResponse> feignResponse = memberApiClient.login(request);
		String accessToken = String.valueOf(feignResponse.getHeaders().get(HttpHeaders.AUTHORIZATION));
		Cookie refreshToken = (Cookie)feignResponse.getHeaders().get(HttpHeaders.SET_COOKIE);

		return new LoginResponse(accessToken, refreshToken);
	}

	@Override
	public void logout() {
		memberApiClient.logout();
	}

	@Override
	public FindLoginIdResponse findLoginId(String loginId) {
		return memberApiClient.findLoginId(loginId);
	}

	@Override
	public ValueDuplicationCheckResponse checkDuplication(ValueDuplicationCheckRequest request) {
		return memberApiClient.checkDuplication(request);
	}

	@Override
	public ResponseCookie createCookie(Token token, Long time) {
		String path;
		String tokenType;
		String sameSite = "Lax";			//중요: https요청에는 none으로 설정해도 됨, http요청은 secure가 false상태이므로 브라우저에서 none에 대한 쿠키는 거부하여 lax로 설정
		if(token instanceof RefreshToken) {
			tokenType = "Refresh-Token";
			path = "/refresh";
		} else if (token instanceof AccessToken) {
			tokenType = "Access-Token";
			path = "/";
		} else {
			throw new DecodingFailureException("토큰 에러");
		}
		return ResponseCookie.from(tokenType, token.getValue())
			.httpOnly(true)					//XSS
			.secure(false)					//중요: 실제 배포 환경에선 https요청으로 변경 / secure -> true
			.sameSite(sameSite)				//CSRF
			.path(path)
			.maxAge(Duration.ofMinutes(time))
			.build();
	}

}
