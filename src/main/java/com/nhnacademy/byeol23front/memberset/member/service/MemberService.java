package com.nhnacademy.byeol23front.memberset.member.service;

import org.springframework.stereotype.Service;

import com.nhnacademy.byeol23front.memberset.member.dto.FindLoginIdResponse;
import com.nhnacademy.byeol23front.memberset.member.dto.LoginRequest;
import com.nhnacademy.byeol23front.memberset.member.dto.LoginResponse;
import com.nhnacademy.byeol23front.memberset.member.dto.LogoutRequest;
import com.nhnacademy.byeol23front.memberset.member.dto.MemberRegisterRequest;
import com.nhnacademy.byeol23front.memberset.member.dto.MemberRegisterResponse;
import com.nhnacademy.byeol23front.memberset.member.dto.ValueDuplicationCheckRequest;
import com.nhnacademy.byeol23front.memberset.member.dto.ValueDuplicationCheckResponse;

@Service
public interface MemberService {
	void register(MemberRegisterRequest request);
	LoginResponse login(LoginRequest request);
	void logout();
	FindLoginIdResponse findLoginId(String loginId);
	ValueDuplicationCheckResponse checkDuplication(ValueDuplicationCheckRequest request);
}
