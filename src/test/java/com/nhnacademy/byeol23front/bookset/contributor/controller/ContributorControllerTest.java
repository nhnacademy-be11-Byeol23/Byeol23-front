package com.nhnacademy.byeol23front.bookset.contributor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.byeol23front.auth.AuthUtil;
import com.nhnacademy.byeol23front.bookset.category.client.CategoryApiClient;
import com.nhnacademy.byeol23front.bookset.contributor.client.ContributorApiClient;
import com.nhnacademy.byeol23front.bookset.contributor.dto.AllContributorResponse;
import com.nhnacademy.byeol23front.bookset.contributor.dto.ContributorCreateRequest;
import com.nhnacademy.byeol23front.bookset.contributor.dto.ContributorUpdateRequest;
import com.nhnacademy.byeol23front.bookset.contributor.dto.PageResponse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import jakarta.servlet.ServletException;

@WebMvcTest(ContributorController.class)
class ContributorControllerTest {

	static class TestPrincipal {
		private final String username;
		private final String nickname;

		TestPrincipal(String username, String nickname) {
			this.username = username;
			this.nickname = nickname;
		}

		public String getUsername() {
			return username;
		}

		public String getNickname() {
			return nickname;
		}
	}


	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockBean
	ContributorApiClient feignClient;

	// CategoryHeaderAdvice 때문에 필요
	@MockBean
	CategoryApiClient categoryApiClient;

	@MockBean(name = "authHelper")
    AuthUtil authUtil;


	// ───────────────────────── GET /admin/cont ─────────────────────────

	@Test
	@DisplayName("GET /admin/cont - 기여자 목록 페이지 렌더링")
	void getAllContributors_returnsViewWithModel() throws Exception {

		MockitoAnnotations.openMocks(this);

		given(authUtil.isLoggedIn()).willReturn(true);
		// given
		AllContributorResponse contributor = new AllContributorResponse(
			1L,
			"홍길동",
			"AUTHOR"
		);

		PageResponse<AllContributorResponse> pageResponse =
			new PageResponse<>(
				List.of(contributor),
				0,
				10,
				true,
				true,
				1,
				1L
			);

		TestPrincipal principal = new TestPrincipal("admin", "관리자닉");
		Authentication auth = new UsernamePasswordAuthenticationToken(
			principal,
			"password",
			List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
		);

		given(feignClient.getAllContributors(0, 10))
			.willReturn(ResponseEntity.ok(pageResponse));

		// when & then
		mockMvc.perform(get("/admin/cont")
				// .with(user("admin").roles("ADMIN"))
				.with(authentication(auth))
				.with(csrf()))
			.andExpect(status().isOk());

		verify(feignClient).getAllContributors(0, 10);
	}


	@Test
	@DisplayName("GET /admin/cont - 클라이언트 예외 발생 시 처리)")
	void getAllContributors_clientError_returnsErrorView() throws Exception {
		// given
		given(feignClient.getAllContributors(anyInt(), anyInt()))
			.willThrow(new RuntimeException("downstream error"));

		// when
		ServletException ex = assertThrows(ServletException.class, () ->
			mockMvc.perform(get("/admin/cont")
					.with(user("admin").roles("ADMIN")))
				.andReturn()
		);

		// then
		Throwable cause = ex.getCause();
		assertNotNull(cause);
		assertTrue(cause instanceof RuntimeException);
		assertEquals("downstream error", cause.getMessage());
	}

	// ───────────────────────── POST /admin/cont (생성) ─────────────────────────

	@Test
	@DisplayName("POST /admin/cont - 기여자 생성 성공 시 redirect")
	void createContributor_createsContributorAndRedirects() throws Exception {
		// given
		ContributorCreateRequest request = new ContributorCreateRequest("홍길동", "AUTHOR");
		// 컨트롤러는 반환값 사용 안 함 → stub 없어도 됨

		// when & then
		mockMvc.perform(post("/admin/cont")
				.with(user("admin").roles("ADMIN"))
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/admin/cont"));

		verify(feignClient).createContributor(any(ContributorCreateRequest.class));
	}

	@Test
	@DisplayName("POST /admin/cont - 기여자 생성 중 클라이언트 예외 발생 시 5xx + error 뷰")
	void createContributor_clientError_returnsErrorView() throws Exception {
		// given
		ContributorCreateRequest request = new ContributorCreateRequest("홍길동", "AUTHOR");

		given(feignClient.createContributor(any(ContributorCreateRequest.class)))
			.willThrow(new RuntimeException("create failed"));

		// when
		ServletException ex = assertThrows(ServletException.class, () ->
			mockMvc.perform(post("/admin/cont")
					.with(user("admin").roles("ADMIN"))
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request)))
				.andReturn()
		);

		// then
		Throwable cause = ex.getCause();
		assertNotNull(cause);
		assertTrue(cause instanceof RuntimeException);
		assertEquals("create failed", cause.getMessage());
	}

	// ───────────────────────── POST /admin/cont/put/{id} (수정) ─────────────────────────

	@Test
	@DisplayName("POST /admin/cont/put/{contributorId} - 기여자 수정 성공")
	void updateContributor_callsFeignClientAndReturnsOk() throws Exception {
		Long contributorId = 1L;
		ContributorUpdateRequest updateRequest = new ContributorUpdateRequest("새 이름", "TRANSLATOR");

		// 반환값을 컨트롤러에서 사용하지 않으므로 stub 불필요

		mockMvc.perform(post("/admin/cont/put/{contributorId}", contributorId)
				.with(user("admin").roles("ADMIN"))
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateRequest)))
			.andExpect(status().isOk())
			.andExpect(content().string(""));

		verify(feignClient).updateContributor(eq(contributorId), any(ContributorUpdateRequest.class));
	}

	@Test
	@DisplayName("POST /admin/cont/put/{contributorId} - 수정 중 클라이언트 예외 발생 시 5xx")
	void updateContributor_clientError_returns5xx() throws Exception {
		// given
		Long contributorId = 1L;
		ContributorUpdateRequest updateRequest = new ContributorUpdateRequest("새 이름", "TRANSLATOR");

		given(feignClient.updateContributor(eq(contributorId), any(ContributorUpdateRequest.class)))
			.willThrow(new RuntimeException("update failed"));

		// when
		ServletException ex = assertThrows(ServletException.class, () ->
			mockMvc.perform(post("/admin/cont/put/{contributorId}", contributorId)
					.with(user("admin").roles("ADMIN"))
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(updateRequest)))
				.andReturn()
		);

		// then
		Throwable cause = ex.getCause();
		assertNotNull(cause);
		assertTrue(cause instanceof RuntimeException);
		assertEquals("update failed", cause.getMessage());
	}

	// ───────────────────────── POST /admin/cont/delete/{id} (삭제) ─────────────────────────

	@Test
	@DisplayName("POST /admin/cont/delete/{contributorId} - 기여자 삭제 성공")
	void deleteContributor_callsFeignClientAndReturnsNoContent() throws Exception {
		// given
		Long contributorId = 1L;

		willDoNothing().given(feignClient).deleteContributor(contributorId);

		// when & then
		mockMvc.perform(post("/admin/cont/delete/{contributorId}", contributorId)
				.with(user("admin").roles("ADMIN"))
				.with(csrf()))
			.andExpect(status().isNoContent())
			.andExpect(content().string(""));

		verify(feignClient).deleteContributor(contributorId);
	}

	@Test
	@DisplayName("POST /admin/cont/delete/{contributorId} - 삭제 중 클라이언트 예외 발생 시 RuntimeException 발생")
	void deleteContributor_clientError_throwsException() throws Exception {
		// given
		Long contributorId = 1L;

		willThrow(new RuntimeException("delete failed"))
			.given(feignClient).deleteContributor(eq(contributorId));

		// when
		ServletException ex = assertThrows(ServletException.class, () ->
			mockMvc.perform(post("/admin/cont/delete/{contributorId}", contributorId)
					.with(user("admin").roles("ADMIN"))
					.with(csrf()))
				.andReturn()  // perform() 안에서 예외가 발생하면 assertThrows 가 잡아준다
		);

		// then: 실제 원인은 RuntimeException("delete failed")
		assertThat(ex.getCause())
			.isInstanceOf(RuntimeException.class)
			.hasMessage("delete failed");
	}

}


