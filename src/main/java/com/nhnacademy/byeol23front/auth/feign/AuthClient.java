package com.nhnacademy.byeol23front.auth.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import com.nhnacademy.byeol23front.memberset.member.dto.ReAuthenticateResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@FeignClient(name = "BYEOL23-GATEWAY", contextId = "AuthApiClient")
@Tag(name = "Auth API", description = "인증 서버 API (토큰 재발급)")
public interface AuthClient {

	@PostMapping("/auth/refresh")
	@Operation(summary = "Access Token 재발급", description = "Refresh Token을 이용하여 새로운 Access Token을 발급받습니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "토큰 재발급 성공",
			content = @Content(schema = @Schema(implementation = ReAuthenticateResponse.class))),
		@ApiResponse(responseCode = "401", description = "Refresh Token 만료 또는 유효하지 않음"),
		@ApiResponse(responseCode = "500", description = "서버 내부 오류")
	})
	ReAuthenticateResponse reissueAccessToken();
}
