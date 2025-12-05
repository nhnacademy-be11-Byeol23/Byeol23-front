package com.nhnacademy.byeol23front.memberset.member.service.Impl;

import com.nhnacademy.byeol23front.memberset.member.client.MemberApiClient;
import com.nhnacademy.byeol23front.memberset.member.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

	@Mock
	private MemberApiClient memberApiClient;

	@InjectMocks
	private MemberServiceImpl memberService;

	private MemberRegisterRequest memberRegisterRequest;
	private LoginRequest loginRequest;
	private LoginResponse loginResponse;
	private FindLoginIdResponse findLoginIdResponse;
	private ValueDuplicationCheckRequest duplicationCheckRequest;
	private ValueDuplicationCheckResponse duplicationCheckResponse;
	private MemberUpdateRequest memberUpdateRequest;
	private MemberUpdateResponse memberUpdateResponse;
	private MemberPasswordUpdateRequest passwordUpdateRequest;
	private MemberPasswordUpdateResponse passwordUpdateResponse;

	@BeforeEach
	void setUp() {
		memberRegisterRequest = new MemberRegisterRequest();
		memberRegisterRequest.setLoginId("testuser");
		memberRegisterRequest.setLoginPassword("Test1234!");
		memberRegisterRequest.setMemberName("홍길동");
		memberRegisterRequest.setNickname("테스트유저");
		memberRegisterRequest.setEmail("test@example.com");
		memberRegisterRequest.setPhoneNumber("010-1234-5678");
		memberRegisterRequest.setBirthDate(LocalDate.of(1990, 1, 1));

		loginRequest = new LoginRequest("testuser", "Test1234!");
		loginResponse = new LoginResponse("access-token", "refresh-token");

		findLoginIdResponse = new FindLoginIdResponse(false);

		duplicationCheckRequest = new ValueDuplicationCheckRequest(
			"testuser", "테스트유저", "010-1234-5678", "test@example.com"
		);
		duplicationCheckResponse = new ValueDuplicationCheckResponse(
			false, false, false, false
		);

		memberUpdateRequest = new MemberUpdateRequest(
			"홍길동", "수정된닉네임", "010-9876-5432", "updated@example.com", LocalDate.of(1990, 1, 1)
		);
		memberUpdateResponse = new MemberUpdateResponse();

		passwordUpdateRequest = new MemberPasswordUpdateRequest("OldPass123!", "NewPass123!");
		passwordUpdateResponse = new MemberPasswordUpdateResponse();
	}

	@Test
	@DisplayName("register - 회원가입 성공")
	void register_Success() {
		willDoNothing().given(memberApiClient).registerRequest(any(MemberRegisterRequest.class));

		memberService.register(memberRegisterRequest);

		verify(memberApiClient).registerRequest(memberRegisterRequest);
	}

	@Test
	@DisplayName("login - 로그인 성공")
	void login_Success() {
		given(memberApiClient.login(any(LoginRequest.class))).willReturn(loginResponse);

		LoginResponse result = memberService.login(loginRequest);

		assertThat(result).isNotNull();
		assertThat(result.accessToken()).isEqualTo("access-token");
		assertThat(result.refreshToken()).isEqualTo("refresh-token");
		verify(memberApiClient).login(loginRequest);
	}

	@Test
	@DisplayName("logout - 로그아웃 성공")
	void logout_Success() {
		willDoNothing().given(memberApiClient).logout();

		memberService.logout();

		verify(memberApiClient).logout();
	}

	@Test
	@DisplayName("findLoginId - 아이디 중복 체크 성공")
	void findLoginId_Success() {
		given(memberApiClient.findLoginId("testuser")).willReturn(findLoginIdResponse);

		FindLoginIdResponse result = memberService.findLoginId("testuser");

		assertThat(result).isNotNull();
		assertThat(result.isDuplicated()).isFalse();
		verify(memberApiClient).findLoginId("testuser");
	}

	@Test
	@DisplayName("checkDuplication - 중복 체크 성공")
	void checkDuplication_Success() {
		given(memberApiClient.checkDuplication(any(ValueDuplicationCheckRequest.class)))
			.willReturn(duplicationCheckResponse);

		ValueDuplicationCheckResponse result = memberService.checkDuplication(duplicationCheckRequest);

		assertThat(result).isNotNull();
		assertThat(result.isDuplicatedId()).isFalse();
		assertThat(result.isDuplicatedNickname()).isFalse();
		assertThat(result.isDuplicatedEmail()).isFalse();
		assertThat(result.isDuplicatedPhoneNumber()).isFalse();
		verify(memberApiClient).checkDuplication(duplicationCheckRequest);
	}

	@Test
	@DisplayName("updateMember - 회원 정보 수정 성공")
	void updateMember_Success() {
		given(memberApiClient.updateMember(any(MemberUpdateRequest.class)))
			.willReturn(memberUpdateResponse);

		MemberUpdateResponse result = memberService.updateMember(memberUpdateRequest);

		assertThat(result).isNotNull();
		verify(memberApiClient).updateMember(memberUpdateRequest);
	}

	@Test
	@DisplayName("updateMemberPassword - 비밀번호 변경 성공")
	void updateMemberPassword_Success() {
		given(memberApiClient.updateMemberPassword(any(MemberPasswordUpdateRequest.class)))
			.willReturn(passwordUpdateResponse);

		MemberPasswordUpdateResponse result = memberService.updateMemberPassword(passwordUpdateRequest);

		assertThat(result).isNotNull();
		verify(memberApiClient).updateMemberPassword(passwordUpdateRequest);
	}

	@Test
	@DisplayName("deleteMember - 회원 탈퇴 성공")
	void deleteMember_Success() {
		willDoNothing().given(memberApiClient).deleteMember();

		memberService.deleteMember();

		verify(memberApiClient).deleteMember();
	}
}

