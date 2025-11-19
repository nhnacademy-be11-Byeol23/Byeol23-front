package com.nhnacademy.byeol23front.memberset.member.client;

import com.nhnacademy.byeol23front.memberset.member.dto.*;

import org.checkerframework.checker.units.qual.C;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


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

	@GetMapping("/api/members")
	ResponseEntity<MemberMyPageResponse> getMember();

	@PutMapping("/api/members")
	ResponseEntity<MemberUpdateResponse> updateMember(@RequestBody MemberUpdateRequest request);

	@PutMapping("/api/members/password")
	ResponseEntity<MemberPasswordUpdateResponse> updateMemberPassword(@RequestBody MemberPasswordUpdateRequest request);

	@PutMapping("api/members/reactivate")
	ResponseEntity<Void> reactivateMember(@RequestBody MemberPasswordUpdateRequest request);

	@DeleteMapping("/api/members")
	ResponseEntity<Void> deleteMember();
}
