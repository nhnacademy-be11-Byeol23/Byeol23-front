package com.nhnacademy.byeol23front.memberset.member.service.Impl;

import java.lang.reflect.Member;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nhnacademy.byeol23front.memberset.member.client.MemberApiClient;
import com.nhnacademy.byeol23front.memberset.member.dto.LoginRequest;
import com.nhnacademy.byeol23front.memberset.member.dto.LoginResponse;
import com.nhnacademy.byeol23front.memberset.member.dto.MemberRegisterRequest;
import com.nhnacademy.byeol23front.memberset.member.dto.MemberRegisterResponse;
import com.nhnacademy.byeol23front.memberset.member.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

	private final MemberApiClient memberApiClient;
	private final PasswordEncoder passwordEncoder;

	@Override
	public MemberRegisterResponse register(MemberRegisterRequest request) {
		request.setPassword(passwordEncoder.encode(request.getPassword()));
		MemberRegisterResponse response = memberApiClient.registerRequest(request).getBody();
		return response;
	}

	@Override
	public LoginResponse login(LoginRequest request) {
		request.setPassword(passwordEncoder.encode(request.getPassword()));
		LoginResponse response = memberApiClient.loginRequest(request).getBody();
		return response;
	}


}
