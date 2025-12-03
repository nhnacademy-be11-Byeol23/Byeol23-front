package com.nhnacademy.byeol23front.memberset.member.service.Impl;

import com.nhnacademy.byeol23front.memberset.member.dto.*;
import org.springframework.stereotype.Service;
import com.nhnacademy.byeol23front.memberset.member.client.MemberApiClient;
import com.nhnacademy.byeol23front.memberset.member.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

	private final MemberApiClient memberApiClient;

	@Override
	public void register(MemberRegisterRequest request) {
		memberApiClient.registerRequest(request);
	}

	@Override
	public LoginResponse login(LoginRequest request) {
		return memberApiClient.login(request);
	}

	@Override
	public void logout() {
		memberApiClient.logout();
	}

	@Override
	public FindLoginIdResponse findLoginId(String loginId) {
		return memberApiClient.findLoginId(loginId);
	}

	@Override
	public ValueDuplicationCheckResponse checkDuplication(ValueDuplicationCheckRequest request) {
		return memberApiClient.checkDuplication(request);
	}

	@Override
	public MemberUpdateResponse updateMember(MemberUpdateRequest request) {
		return memberApiClient.updateMember(request);
	}

	@Override
	public MemberPasswordUpdateResponse updateMemberPassword(MemberPasswordUpdateRequest request) {
		return memberApiClient.updateMemberPassword(request);
	}

	@Override
	public void deleteMember() {
		memberApiClient.deleteMember();
	}

	@Override
	public ReAuthenticateResponse reissueAccessToken(String refreshToken) {
		return memberApiClient.reissueAccessToken(refreshToken);
	}

}
