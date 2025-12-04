package com.nhnacademy.byeol23front.memberset.member.service;

import com.nhnacademy.byeol23front.memberset.member.dto.*;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import com.nhnacademy.byeol23front.memberset.domain.Token;
import org.springframework.web.bind.annotation.RequestHeader;

@Service
public interface MemberService {
	void register(MemberRegisterRequest request);
	LoginResponse login(LoginRequest request);
	void logout();
	FindLoginIdResponse findLoginId(String loginId);
	ValueDuplicationCheckResponse checkDuplication(ValueDuplicationCheckRequest request);
	MemberUpdateResponse updateMember(MemberUpdateRequest request);
	MemberPasswordUpdateResponse updateMemberPassword(MemberPasswordUpdateRequest request);
	void deleteMember();
}
