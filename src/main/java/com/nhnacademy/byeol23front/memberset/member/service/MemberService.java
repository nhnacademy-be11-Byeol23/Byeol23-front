package com.nhnacademy.byeol23front.memberset.member.service;

import org.springframework.stereotype.Service;

import com.nhnacademy.byeol23front.memberset.member.dto.LoginRequest;
import com.nhnacademy.byeol23front.memberset.member.dto.LoginResponse;
import com.nhnacademy.byeol23front.memberset.member.dto.MemberRegisterRequest;
import com.nhnacademy.byeol23front.memberset.member.dto.MemberRegisterResponse;

@Service
public interface MemberService {
	void register(MemberRegisterRequest request);
	LoginResponse login(LoginRequest request);
}
