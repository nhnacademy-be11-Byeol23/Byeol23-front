package com.nhnacademy.byeol23front.memberset.member.client;

import com.nhnacademy.byeol23front.memberset.member.dto.*;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "BYEOL23-GATEWAY", contextId = "MemberApiClient")
public interface MemberApiClient {
	/**
	 * 회원 가입
	 */
	@PostMapping("/api/members")
	Void registerRequest(@RequestBody MemberRegisterRequest request);
	/**
	 * 로그인
	 */
	@PostMapping("/auth/login")
	LoginResponse login(@RequestBody LoginRequest request);

	@GetMapping("/auth/payco/login")
	Void loginWithPayco();

	@PostMapping("/auth/logout")
	Void logout();

	@GetMapping("/api/members")
	MemberMyPageResponse getMember();

	@PostMapping("/api/members/put")
	MemberUpdateResponse updateMember(@RequestBody MemberUpdateRequest request);

	@PostMapping("/api/members/put/password")
	MemberPasswordUpdateResponse updateMemberPassword(@RequestBody MemberPasswordUpdateRequest request);

	@PutMapping("api/members/reactivate")
	void reactivateMember(@RequestBody MemberPasswordUpdateRequest request);

	@PostMapping("/api/members/delete")
	void deleteMember();

	@GetMapping("/api/members/check-id")
	FindLoginIdResponse findLoginId(@RequestParam("loginId") String loginId);

	@PostMapping("/api/members/check-duplication")
	ValueDuplicationCheckResponse checkDuplication(@RequestBody ValueDuplicationCheckRequest request);

	@PostMapping("/auth/social-login")
	LoginResponse socialLogin(SocialLoginRequest request);


}
