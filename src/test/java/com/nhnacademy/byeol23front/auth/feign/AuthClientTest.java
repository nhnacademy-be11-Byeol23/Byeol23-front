package com.nhnacademy.byeol23front.auth.feign;

import com.nhnacademy.byeol23front.memberset.member.dto.ReAuthenticateResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AuthClientTest {

	@Mock
	private AuthClient authClient;

	private String testNewAccessToken;

	@BeforeEach
	void setUp() {
		testNewAccessToken = "new-access-token";
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
}
