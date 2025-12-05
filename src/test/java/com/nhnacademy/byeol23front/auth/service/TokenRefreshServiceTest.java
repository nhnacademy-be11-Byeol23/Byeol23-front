package com.nhnacademy.byeol23front.auth.service;

import com.nhnacademy.byeol23front.auth.feign.AuthClient;
import com.nhnacademy.byeol23front.auth.feign.TokenContext;
import com.nhnacademy.byeol23front.commons.exception.ExpiredRefreshTokenException;
import com.nhnacademy.byeol23front.memberset.member.dto.ReAuthenticateResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TokenRefreshServiceTest {

	@Mock
	private AuthClient authClient;

	@InjectMocks
	private TokenRefreshService tokenRefreshService;

	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private String testRefreshToken;
	private String testNewAccessToken;

	@BeforeEach
	void setUp() {
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		testRefreshToken = "test-refresh-token";
		testNewAccessToken = "new-access-token";

		// RequestContextHolder 설정
		ServletRequestAttributes attributes = new ServletRequestAttributes(request, response);
		RequestContextHolder.setRequestAttributes(attributes);
	}

	@Test
	@DisplayName("refreshTokens - 토큰 재발급 성공")
	void refreshTokens_Success() {
		// given
		Cookie refreshCookie = new Cookie("Refresh-Token", testRefreshToken);
		request.setCookies(refreshCookie);

		ReAuthenticateResponse reissueResponse = new ReAuthenticateResponse(testNewAccessToken);
		given(authClient.reissueAccessToken()).willReturn(reissueResponse);

		// when
		String result = tokenRefreshService.refreshTokens();

		// then
		assertThat(result).isEqualTo(testNewAccessToken);
		assertThat(TokenContext.get()).isEqualTo(testNewAccessToken);
		verify(authClient).reissueAccessToken();
	}

	@Test
	@DisplayName("refreshTokens - Refresh-Token 쿠키가 없는 경우")
	void refreshTokens_NoRefreshTokenCookie() {
		// given
		request.setCookies(); // 쿠키 없음

		// when
		String result = tokenRefreshService.refreshTokens();

		// then
		assertThat(result).isNull();
		verify(authClient, never()).reissueAccessToken();
	}

	@Test
	@DisplayName("refreshTokens - Refresh-Token 쿠키가 비어있는 경우")
	void refreshTokens_EmptyRefreshTokenCookie() {
		// given
		Cookie refreshCookie = new Cookie("Refresh-Token", "");
		request.setCookies(refreshCookie);

		// when
		String result = tokenRefreshService.refreshTokens();

		// then
		assertThat(result).isNull();
		verify(authClient, never()).reissueAccessToken();
	}

	@Test
	@DisplayName("refreshTokens - Refresh Token 만료된 경우")
	void refreshTokens_ExpiredRefreshToken() {
		// given
		Cookie refreshCookie = new Cookie("Refresh-Token", testRefreshToken);
		request.setCookies(refreshCookie);

		given(authClient.reissueAccessToken())
			.willThrow(new ExpiredRefreshTokenException("Refresh token expired"));

		// when & then
		assertThatThrownBy(() -> tokenRefreshService.refreshTokens())
			.isInstanceOf(ExpiredRefreshTokenException.class)
			.hasMessageContaining("Refresh token expired");
	}

	@Test
	@DisplayName("refreshTokens - 재발급 응답이 null인 경우")
	void refreshTokens_NullResponse() {
		// given
		Cookie refreshCookie = new Cookie("Refresh-Token", testRefreshToken);
		request.setCookies(refreshCookie);

		given(authClient.reissueAccessToken()).willReturn(null);

		// when
		String result = tokenRefreshService.refreshTokens();

		// then
		assertThat(result).isNull();
	}

	@Test
	@DisplayName("refreshTokens - 재발급 응답의 newAccessToken이 null인 경우")
	void refreshTokens_NullNewAccessToken() {
		// given
		Cookie refreshCookie = new Cookie("Refresh-Token", testRefreshToken);
		request.setCookies(refreshCookie);

		ReAuthenticateResponse reissueResponse = new ReAuthenticateResponse(null);
		given(authClient.reissueAccessToken()).willReturn(reissueResponse);

		// when
		String result = tokenRefreshService.refreshTokens();

		// then
		assertThat(result).isNull();
	}

	@Test
	@DisplayName("refreshTokens - 재발급 응답의 newAccessToken이 빈 문자열인 경우")
	void refreshTokens_EmptyNewAccessToken() {
		// given
		Cookie refreshCookie = new Cookie("Refresh-Token", testRefreshToken);
		request.setCookies(refreshCookie);

		ReAuthenticateResponse reissueResponse = new ReAuthenticateResponse("");
		given(authClient.reissueAccessToken()).willReturn(reissueResponse);

		// when
		String result = tokenRefreshService.refreshTokens();

		// then
		assertThat(result).isNull();
	}

	@Test
	@DisplayName("refreshTokens - RequestAttributes가 null인 경우")
	void refreshTokens_NullRequestAttributes() {
		// given
		RequestContextHolder.resetRequestAttributes();

		// when
		String result = tokenRefreshService.refreshTokens();

		// then
		assertThat(result).isNull();
		verify(authClient, never()).reissueAccessToken();
	}

	@Test
	@DisplayName("refreshTokens - 일반 예외 발생 시 null 반환")
	void refreshTokens_GeneralException() {
		// given
		Cookie refreshCookie = new Cookie("Refresh-Token", testRefreshToken);
		request.setCookies(refreshCookie);

		given(authClient.reissueAccessToken())
			.willThrow(new RuntimeException("Network error"));

		// when
		String result = tokenRefreshService.refreshTokens();

		// then
		assertThat(result).isNull();
	}
}

