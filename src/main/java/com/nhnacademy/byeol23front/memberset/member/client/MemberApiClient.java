package com.nhnacademy.byeol23front.memberset.member.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.nhnacademy.byeol23front.memberset.member.dto.LoginRequest;
import com.nhnacademy.byeol23front.memberset.member.dto.LoginResponse;
import com.nhnacademy.byeol23front.memberset.member.dto.MemberRegisterRequest;
import com.nhnacademy.byeol23front.memberset.member.dto.MemberRegisterResponse;


@FeignClient(name = "memberApiClient", url = "${backend.api.url}")
public interface MemberApiClient {
	/**
	 * 회원 가입
	 */
	@PostMapping("/api/members/register")
	ResponseEntity<MemberRegisterResponse> registerRequest(@RequestBody MemberRegisterRequest request);

	/**
	 * 로그인
	 */
	@PostMapping("/api/members/login")
	ResponseEntity<LoginResponse> loginRequest(@RequestBody LoginRequest request);
}
