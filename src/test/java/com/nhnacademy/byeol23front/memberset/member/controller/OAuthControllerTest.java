package com.nhnacademy.byeol23front.memberset.member.controller;

import com.nhnacademy.byeol23front.auth.AuthUtil;
import com.nhnacademy.byeol23front.auth.CookieProperties;
import com.nhnacademy.byeol23front.bookset.category.client.CategoryApiClient;
import com.nhnacademy.byeol23front.memberset.member.client.MemberApiClient;
import com.nhnacademy.byeol23front.memberset.member.dto.*;
import com.nhnacademy.byeol23front.memberset.member.service.MemberService;
import com.nhnacademy.byeol23front.memberset.member.service.PaycoOAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OAuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class OAuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private PaycoOAuthService paycoOAuthService;

	@MockBean
	private MemberApiClient memberApiClient;

	@MockBean
	private MemberService memberService;

	@MockBean
	private CookieProperties cookieProperties;

	@MockBean
	private CategoryApiClient categoryApiClient;

	@MockBean(name = "authHelper")
	private AuthUtil authUtil;

	private String testState;
	private String testCode;
	private PaycoTokenResponse paycoTokenResponse;
	private PaycoUserInfo paycoUserInfo;
	private FindLoginIdResponse findLoginIdResponse;
	private LoginResponse loginResponse;

	@BeforeEach
	void setUp() {
		testState = "test-state-12345";
		testCode = "test-authorization-code";

		paycoTokenResponse = new PaycoTokenResponse(
			"payco-access-token",
			"payco-secret",
			"Bearer",
			3600L,
			"payco-refresh-token"
		);

		paycoUserInfo = new PaycoUserInfo(
			"payco123",
			"payco@example.com",
			"010-1234-5678",
			"페이코유저",
			"0101"
		);

		findLoginIdResponse = new FindLoginIdResponse(true);
		loginResponse = new LoginResponse("access-token", "refresh-token");

		given(cookieProperties.isSecure()).willReturn(false);
		given(cookieProperties.getSameSite()).willReturn("Lax");
		given(authUtil.isLoggedIn()).willReturn(false);
	}

	@Test
	@DisplayName("GET /payco/login - 페이코 로그인 페이지로 리다이렉트 성공")
	void redirectToPayco_Success() throws Exception {
		given(paycoOAuthService.generateState()).willReturn(testState);
		given(paycoOAuthService.buildAuthorizeUrl(testState))
			.willReturn("https://payco.com/authorize?state=" + testState);

		mockMvc.perform(get("/payco/login"))
			.andExpect(status().isFound())
			.andExpect(header().string(HttpHeaders.LOCATION, "https://payco.com/authorize?state=" + testState))
			.andExpect(cookie().exists("PAYCO_STATE"))
			.andExpect(cookie().value("PAYCO_STATE", testState));

		verify(paycoOAuthService).generateState();
		verify(paycoOAuthService).buildAuthorizeUrl(testState);
	}

	@Test
	@DisplayName("GET /payco/callback - 기존 회원 소셜 로그인 성공")
	void paycoCallback_ExistingMember_Success() throws Exception {
		given(paycoOAuthService.issueTokenFromPayco(testCode)).willReturn(paycoTokenResponse);
		given(paycoOAuthService.getUserInfo("payco-access-token")).willReturn(paycoUserInfo);
		given(memberService.findLoginId("payco123")).willReturn(findLoginIdResponse);
		given(memberApiClient.socialLogin(any(SocialLoginRequest.class))).willReturn(loginResponse);

		mockMvc.perform(get("/payco/callback")
				.param("code", testCode))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/"))
			.andExpect(cookie().exists("Access-Token"))
			.andExpect(cookie().exists("Refresh-Token"));

		verify(paycoOAuthService).issueTokenFromPayco(testCode);
		verify(paycoOAuthService).getUserInfo("payco-access-token");
		verify(memberService).findLoginId("payco123");
		verify(memberApiClient).socialLogin(any(SocialLoginRequest.class));
	}

	@Test
	@DisplayName("GET /payco/callback - 신규 회원 회원가입 페이지로 리다이렉트")
	void paycoCallback_NewMember_RedirectToRegister() throws Exception {
		FindLoginIdResponse notDuplicatedResponse = new FindLoginIdResponse(false);

		given(paycoOAuthService.issueTokenFromPayco(testCode)).willReturn(paycoTokenResponse);
		given(paycoOAuthService.getUserInfo("payco-access-token")).willReturn(paycoUserInfo);
		given(memberService.findLoginId("payco123")).willReturn(notDuplicatedResponse);

		mockMvc.perform(get("/payco/callback")
				.param("code", testCode))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("register"));

		verify(paycoOAuthService).issueTokenFromPayco(testCode);
		verify(paycoOAuthService).getUserInfo("payco-access-token");
		verify(memberService).findLoginId("payco123");
	}

	@Test
	@DisplayName("GET /payco/register - 페이코 회원가입 폼 반환 성공")
	void paycoRegister_Success() throws Exception {
		mockMvc.perform(get("/payco/register")
				.flashAttr("userInfo", paycoUserInfo)
				.flashAttr("form", new MemberRegisterRequest()))
			.andExpect(status().isOk())
			.andExpect(view().name("member/payco-register"));
	}
}

