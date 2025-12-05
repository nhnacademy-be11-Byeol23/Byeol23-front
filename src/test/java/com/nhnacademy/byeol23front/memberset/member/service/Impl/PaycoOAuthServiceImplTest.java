package com.nhnacademy.byeol23front.memberset.member.service.Impl;

import com.nhnacademy.byeol23front.memberset.member.dto.PaycoTokenResponse;
import com.nhnacademy.byeol23front.memberset.member.dto.PaycoUserInfo;
import com.nhnacademy.byeol23front.memberset.member.dto.PaycoUserInfoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PaycoOAuthServiceImplTest {

	@Mock
	private RestTemplate restTemplate;

	private PaycoOAuthServiceImpl paycoOAuthService;

	private String clientId;
	private String clientSecret;
	private String redirectURI;
	private String authorizeURI;
	private String tokenURI;
	private String userInfoURI;
	private String testCode;
	private String testState;
	private PaycoTokenResponse paycoTokenResponse;
	private PaycoUserInfo paycoUserInfo;
	private PaycoUserInfoResponse paycoUserInfoResponse;

	@BeforeEach
	void setUp() {
		paycoOAuthService = new PaycoOAuthServiceImpl();
		
		clientId = "test-client-id";
		clientSecret = "test-client-secret";
		redirectURI = "http://localhost:8080/payco/callback";
		authorizeURI = "https://payco.com/authorize";
		tokenURI = "https://payco.com/token";
		userInfoURI = "https://payco.com/userinfo";

		ReflectionTestUtils.setField(paycoOAuthService, "clientId", clientId);
		ReflectionTestUtils.setField(paycoOAuthService, "clientSecret", clientSecret);
		ReflectionTestUtils.setField(paycoOAuthService, "redirectURI", redirectURI);
		ReflectionTestUtils.setField(paycoOAuthService, "authorizeURI", authorizeURI);
		ReflectionTestUtils.setField(paycoOAuthService, "tokenURI", tokenURI);
		ReflectionTestUtils.setField(paycoOAuthService, "userInfoURI", userInfoURI);
		ReflectionTestUtils.setField(paycoOAuthService, "restTemplate", restTemplate);

		testCode = "test-authorization-code";
		testState = "test-state-12345";

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

		PaycoUserInfoResponse.Header header = new PaycoUserInfoResponse.Header(true, 200, "Success");
		PaycoUserInfoResponse.Data data = new PaycoUserInfoResponse.Data(paycoUserInfo);
		paycoUserInfoResponse = new PaycoUserInfoResponse(header, data);
	}

	@Test
	@DisplayName("generateState - STATE 생성 성공")
	void generateState_Success() {
		String state1 = paycoOAuthService.generateState();
		String state2 = paycoOAuthService.generateState();

		assertThat(state1).isNotNull();
		assertThat(state2).isNotNull();
		assertThat(state1).isNotEqualTo(state2);
	}

	@Test
	@DisplayName("buildAuthorizeUrl - 인증 URL 생성 성공")
	void buildAuthorizeUrl_Success() {
		String url = paycoOAuthService.buildAuthorizeUrl(testState);

		assertThat(url).isNotNull();
		assertThat(url).contains(authorizeURI);
		assertThat(url).contains("response_type=code");
		assertThat(url).contains("client_id=" + clientId);
		assertThat(url).contains("serviceProviderCode=FRIENDS");
		assertThat(url).contains("redirect_uri=");
		assertThat(url).contains("state=" + testState);
		assertThat(url).contains("userLocale=ko_KR");
	}

	@Test
	@DisplayName("issueTokenFromPayco - 토큰 발급 성공")
	void issueTokenFromPayco_Success() {
		ResponseEntity<PaycoTokenResponse> responseEntity = 
			new ResponseEntity<>(paycoTokenResponse, HttpStatus.OK);

		given(restTemplate.postForEntity(
			eq(tokenURI),
			any(HttpEntity.class),
			eq(PaycoTokenResponse.class)
		)).willReturn(responseEntity);

		PaycoTokenResponse result = paycoOAuthService.issueTokenFromPayco(testCode);

		assertThat(result).isNotNull();
		assertThat(result.accessToken()).isEqualTo("payco-access-token");
		assertThat(result.secret()).isEqualTo("payco-secret");
		assertThat(result.tokenType()).isEqualTo("Bearer");
		assertThat(result.expiration()).isEqualTo(3600L);
		assertThat(result.refreshToken()).isEqualTo("payco-refresh-token");
	}

	@Test
	@DisplayName("getUserInfo - 사용자 정보 조회 성공")
	void getUserInfo_Success() {
		ResponseEntity<PaycoUserInfoResponse> responseEntity = 
			new ResponseEntity<>(paycoUserInfoResponse, HttpStatus.OK);

		given(restTemplate.exchange(
			eq(userInfoURI),
			eq(HttpMethod.POST),
			any(HttpEntity.class),
			eq(PaycoUserInfoResponse.class)
		)).willReturn(responseEntity);

		PaycoUserInfo result = paycoOAuthService.getUserInfo("payco-access-token");

		assertThat(result).isNotNull();
		assertThat(result.paycoId()).isEqualTo("payco123");
		assertThat(result.email()).isEqualTo("payco@example.com");
		assertThat(result.phoneNumber()).isEqualTo("010-1234-5678");
		assertThat(result.name()).isEqualTo("페이코유저");
		assertThat(result.birthdate()).isEqualTo("0101");
	}

	@Test
	@DisplayName("getUserInfo - 응답 body가 null인 경우")
	void getUserInfo_NullBody() {
		ResponseEntity<PaycoUserInfoResponse> responseEntity = 
			new ResponseEntity<>(null, HttpStatus.OK);

		given(restTemplate.exchange(
			eq(userInfoURI),
			eq(HttpMethod.POST),
			any(HttpEntity.class),
			eq(PaycoUserInfoResponse.class)
		)).willReturn(responseEntity);

		PaycoUserInfo result = paycoOAuthService.getUserInfo("payco-access-token");

		assertThat(result).isNull();
	}

	@Test
	@DisplayName("getUserInfo - data가 null인 경우")
	void getUserInfo_NullData() {
		PaycoUserInfoResponse.Header header = new PaycoUserInfoResponse.Header(true, 200, "Success");
		PaycoUserInfoResponse nullDataResponse = new PaycoUserInfoResponse(header, null);
		ResponseEntity<PaycoUserInfoResponse> responseEntity = 
			new ResponseEntity<>(nullDataResponse, HttpStatus.OK);

		given(restTemplate.exchange(
			eq(userInfoURI),
			eq(HttpMethod.POST),
			any(HttpEntity.class),
			eq(PaycoUserInfoResponse.class)
		)).willReturn(responseEntity);

		PaycoUserInfo result = paycoOAuthService.getUserInfo("payco-access-token");

		assertThat(result).isNull();
	}
}

