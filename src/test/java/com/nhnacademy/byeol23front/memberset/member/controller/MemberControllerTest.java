package com.nhnacademy.byeol23front.memberset.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.byeol23front.auth.AuthUtil;
import com.nhnacademy.byeol23front.auth.CookieProperties;
import com.nhnacademy.byeol23front.bookset.category.client.CategoryApiClient;
import com.nhnacademy.byeol23front.memberset.member.dto.*;
import com.nhnacademy.byeol23front.memberset.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberController.class)
@AutoConfigureMockMvc(addFilters = false)
class MemberControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private MemberService memberService;

	@MockBean
	private CookieProperties cookieProperties;

	@MockBean
	private CategoryApiClient categoryApiClient;

	@MockBean(name = "authHelper")
	private AuthUtil authUtil;

	private ObjectMapper objectMapper;
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
		objectMapper = new ObjectMapper();
		objectMapper.findAndRegisterModules();
		
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

		given(cookieProperties.isSecure()).willReturn(false);
		given(cookieProperties.getSameSite()).willReturn("Lax");
		given(authUtil.isLoggedIn()).willReturn(false);
	}

	@Test
	@DisplayName("GET /members/register - 회원가입 폼 화면 반환 성공")
	void showRegisterForm_Success() throws Exception {
		mockMvc.perform(get("/members/register"))
			.andExpect(status().isOk())
			.andExpect(view().name("member/register"));
	}

	@Test
	@DisplayName("POST /members/register - 회원가입 성공")
	void register_Success() throws Exception {
		willDoNothing().given(memberService).register(any(MemberRegisterRequest.class));

		mockMvc.perform(post("/members/register")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("loginId", "testuser")
				.param("loginPassword", "Test1234!")
				.param("memberName", "홍길동")
				.param("nickname", "테스트유저")
				.param("email", "test@example.com")
				.param("phoneNumber", "010-1234-5678")
				.param("birthDate", "1990-01-01"))
			.andExpect(status().isOk())
			.andExpect(view().name("member/login"));

		verify(memberService).register(any(MemberRegisterRequest.class));
	}

	@Test
	@DisplayName("GET /members/login - 로그인 폼 화면 반환 성공")
	void showLoginForm_Success() throws Exception {
		mockMvc.perform(get("/members/login"))
			.andExpect(status().isOk())
			.andExpect(view().name("member/login"));
	}

	@Test
	@DisplayName("GET /members/login - bookId와 quantity 파라미터 포함")
	void showLoginForm_WithBookIdAndQuantity() throws Exception {
		mockMvc.perform(get("/members/login")
				.param("bookId", "1")
				.param("quantity", "2"))
			.andExpect(status().isOk())
			.andExpect(view().name("member/login"));
	}

	@Test
	@DisplayName("POST /members/login - 로그인 성공")
	void login_Success() throws Exception {
		given(memberService.login(any(LoginRequest.class))).willReturn(loginResponse);

		mockMvc.perform(post("/members/login")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("loginId", "testuser")
				.param("loginPassword", "Test1234!"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/"))
			.andExpect(cookie().exists("Access-Token"))
			.andExpect(cookie().exists("Refresh-Token"));

		verify(memberService).login(any(LoginRequest.class));
	}

	@Test
	@DisplayName("POST /members/login - 로그인 성공 후 주문 페이지로 리다이렉트")
	void login_Success_RedirectToOrder() throws Exception {
		given(memberService.login(any(LoginRequest.class))).willReturn(loginResponse);

		mockMvc.perform(post("/members/login")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("loginId", "testuser")
				.param("loginPassword", "Test1234!")
				.param("bookIds", "1")
				.param("quantities", "2"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/orders/direct?bookId=1&quantity=2"))
			.andExpect(cookie().exists("Access-Token"))
			.andExpect(cookie().exists("Refresh-Token"));
	}

	@Test
	@DisplayName("POST /members/logout - 로그아웃 성공")
	void logout_Success() throws Exception {
		willDoNothing().given(memberService).logout();

		mockMvc.perform(post("/members/logout")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/"))
			.andExpect(cookie().maxAge("Access-Token", 0))
			.andExpect(cookie().maxAge("Refresh-Token", 0));

		verify(memberService).logout();
	}

	@Test
	@DisplayName("GET /members/check-id - 아이디 중복 체크 성공")
	void findLoginId_Success() throws Exception {
		given(memberService.findLoginId("testuser")).willReturn(findLoginIdResponse);

		mockMvc.perform(get("/members/check-id")
				.param("loginId", "testuser"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isDuplicated").value(false));

		verify(memberService).findLoginId("testuser");
	}

	@Test
	@DisplayName("POST /members/check-duplication - 중복 체크 성공")
	void checkDuplication_Success() throws Exception {
		given(memberService.checkDuplication(any(ValueDuplicationCheckRequest.class)))
			.willReturn(duplicationCheckResponse);

		mockMvc.perform(post("/members/check-duplication")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(duplicationCheckRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isDuplicatedId").value(false))
			.andExpect(jsonPath("$.isDuplicatedNickname").value(false))
			.andExpect(jsonPath("$.isDuplicatedEmail").value(false))
			.andExpect(jsonPath("$.isDuplicatedPhoneNumber").value(false));

		verify(memberService).checkDuplication(any(ValueDuplicationCheckRequest.class));
	}

	@Test
	@DisplayName("POST /members/put - 회원 정보 수정 성공")
	void updateMember_Success() throws Exception {
		given(memberService.updateMember(any(MemberUpdateRequest.class)))
			.willReturn(memberUpdateResponse);

		mockMvc.perform(post("/members/put")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(memberUpdateRequest)))
			.andExpect(status().isOk());

		verify(memberService).updateMember(any(MemberUpdateRequest.class));
	}

	@Test
	@DisplayName("POST /members/put/password - 비밀번호 변경 성공")
	void updatePassword_Success() throws Exception {
		given(memberService.updateMemberPassword(any(MemberPasswordUpdateRequest.class)))
			.willReturn(passwordUpdateResponse);

		mockMvc.perform(post("/members/put/password")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(passwordUpdateRequest)))
			.andExpect(status().isOk());

		verify(memberService).updateMemberPassword(any(MemberPasswordUpdateRequest.class));
	}

	@Test
	@DisplayName("POST /members/delete - 회원 탈퇴 성공")
	void deleteMember_Success() throws Exception {
		willDoNothing().given(memberService).deleteMember();

		mockMvc.perform(post("/members/delete"))
			.andExpect(status().isNoContent());

		verify(memberService).deleteMember();
	}
}

