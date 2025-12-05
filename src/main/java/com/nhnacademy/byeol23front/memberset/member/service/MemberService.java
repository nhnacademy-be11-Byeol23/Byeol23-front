package com.nhnacademy.byeol23front.memberset.member.service;

import com.nhnacademy.byeol23front.memberset.member.dto.*;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.stereotype.Service;

/**
 * 프론트 서버에서 회원 관련 요청을 백엔드/인증 서버로 포워딩하는 서비스 인터페이스입니다.
 *
 * <p>회원 가입, 로그인/로그아웃, 정보 수정, 중복 체크 등의 기능을 제공합니다.</p>
 */
@Schema(name = "FrontMemberService", description = "프론트 서버 회원 도메인 비즈니스 로직 인터페이스")
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
