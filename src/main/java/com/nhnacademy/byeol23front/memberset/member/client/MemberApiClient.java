package com.nhnacademy.byeol23front.memberset.member.client;

import com.nhnacademy.byeol23front.memberset.member.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


@FeignClient(name = "BYEOL23-GATEWAY", contextId = "MemberApiClient")
public interface MemberApiClient {
	/**
	 * 회원 가입
	 */
	@PostMapping("/api/members/register")
	ResponseEntity<MemberRegisterResponse> registerRequest(@RequestBody MemberRegisterRequest request);

	/**
	 * 로그인
	 */
	@PostMapping("/auth/login")
	ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request);

	@PostMapping("/auth/logout")
	ResponseEntity<LogoutResponse> logout();

	/**
	 * 마이페이지에서 회원 정보를 가져오는 메서드
	 * */
	@GetMapping("/api/members")
	ResponseEntity<MemberMyPageResponse> getMember(@CookieValue(name = "Access-Token") String token);
}
