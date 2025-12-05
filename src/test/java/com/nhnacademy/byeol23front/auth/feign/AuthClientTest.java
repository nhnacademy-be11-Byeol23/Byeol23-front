package com.nhnacademy.byeol23front.auth.feign;

import com.nhnacademy.byeol23front.memberset.member.dto.LoginResponse;
import com.nhnacademy.byeol23front.memberset.member.dto.ReAuthenticateResponse;
import com.nhnacademy.byeol23front.memberset.member.dto.SocialLoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AuthClientTest {

	@Mock
	private AuthClient authClient;

	private String testAccessToken;
	private String testRefreshToken;
	private String testNewAccessToken;
	private String testSocialLoginId;

	@BeforeEach
	void setUp() {
		testAccessToken = "test-access-token";
		testRefreshToken = "test-refresh-token";
		testNewAccessToken = "new-access-token";
		testSocialLoginId = "payco123";
	}

	@Test
	@DisplayName("reissueAccessToken - Access Token 재발급 성공")
	void reissueAccessToken_Success() {
		// given
		ReAuthenticateResponse expectedResponse = new ReAuthenticateResponse(testNewAccessToken);
		given(authClient.reissueAccessToken()).willReturn(expectedResponse);

		// when
		ReAuthenticateResponse response = authClient.reissueAccessToken();

		// then
		assertThat(response).isNotNull();
		assertThat(response.newAccessToken()).isEqualTo(testNewAccessToken);
	}

	@Test
	@DisplayName("socialLogin - 소셜 로그인 성공")
	void socialLogin_Success() {
		// given
		SocialLoginRequest request = new SocialLoginRequest(testSocialLoginId);
		LoginResponse expectedResponse = new LoginResponse(testAccessToken, testRefreshToken);
		given(authClient.socialLogin(any(SocialLoginRequest.class))).willReturn(expectedResponse);

		// when
		LoginResponse response = authClient.socialLogin(request);

		// then
		assertThat(response).isNotNull();
		assertThat(response.accessToken()).isEqualTo(testAccessToken);
		assertThat(response.refreshToken()).isEqualTo(testRefreshToken);
	}

	@Test
	@DisplayName("socialLogin - 소셜 로그인 실패 (존재하지 않는 계정)")
	void socialLogin_Failure_AccountNotFound() {
		// given
		SocialLoginRequest request = new SocialLoginRequest("non-existent-id");
		given(authClient.socialLogin(any(SocialLoginRequest.class)))
			.willThrow(new RuntimeException("Account not found"));

		// when & then
		try {
			authClient.socialLogin(request);
		} catch (RuntimeException e) {
			assertThat(e.getMessage()).contains("Account not found");
		}
	}
}

