package com.nhnacademy.byeol23front.auth.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import com.nhnacademy.byeol23front.memberset.member.dto.ReAuthenticateResponse;

@FeignClient(name = "BYEOL23-GATEWAY", contextId = "AuthApiClient")
public interface AuthClient {

	@PostMapping("/auth/refresh")
	ReAuthenticateResponse reissueAccessToken();
}
