package com.nhnacademy.byeol23front.memberset.member.client;

import com.nhnacademy.byeol23front.memberset.member.dto.*;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

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
	LogoutResponse logout();

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

	@GetMapping("/api/members/check-duplication")
	ValueDuplicationCheckResponse checkDuplication(@RequestBody ValueDuplicationCheckRequest request);

	@PostMapping("/auth/social-login")
	Void socialLogin(SocialLoginRequest request);

	@PostMapping("/auth/refresh")
	ReAuthenticateResponse reissueAccessToken(@RequestBody String refreshToken);
}
