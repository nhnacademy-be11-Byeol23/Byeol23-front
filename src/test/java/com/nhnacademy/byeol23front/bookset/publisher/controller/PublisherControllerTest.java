package com.nhnacademy.byeol23front.bookset.publisher.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import jakarta.servlet.ServletException;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.byeol23front.auth.AuthHelper;
import com.nhnacademy.byeol23front.bookset.category.client.CategoryApiClient;
import com.nhnacademy.byeol23front.bookset.publisher.client.PublisherApiClient;
import com.nhnacademy.byeol23front.bookset.publisher.dto.AllPublishersInfoResponse;
import com.nhnacademy.byeol23front.bookset.publisher.dto.PageResponse;
import com.nhnacademy.byeol23front.bookset.publisher.dto.PublisherCreateRequest;
import com.nhnacademy.byeol23front.bookset.publisher.dto.PublisherUpdateRequest;

@WebMvcTest(PublisherController.class)
class PublisherControllerTest {

	// ───────────────────────── TestPrincipal (for Thymeleaf header) ─────────────────────────
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
	PublisherApiClient feignClient;

	// CategoryHeaderAdvice 때문에 필요
	@MockBean
	CategoryApiClient categoryApiClient;

	// common_header에서 @authHelper 사용
	@MockBean(name = "authHelper")
	AuthHelper authHelper;

	// ───────────────────────── GET /admin/pub ─────────────────────────

	@Test
	@DisplayName("GET /admin/pub - 출판사 목록 페이지 렌더링")
	void getPublishers_returnsViewWithModel() throws Exception {
		MockitoAnnotations.openMocks(this);

		// header에서 로그인 여부 체크
		given(authHelper.isLoggedIn()).willReturn(true);

		// given
		AllPublishersInfoResponse publisher = new AllPublishersInfoResponse(
			1L,
			"출판사이름"
		); // 필드 개수에 맞게 수정 필요하면 여기만 바꾸면 됨

		PageResponse<AllPublishersInfoResponse> pageResponse =
			new PageResponse<>(
				List.of(publisher), // content
				0,                  // number
				10,                 // size
				1L,                 // totalElements
				1,                  // totalPages
				true,               // first
				true                // last
			);

		TestPrincipal principal = new TestPrincipal("admin", "관리자닉");
		Authentication auth = new UsernamePasswordAuthenticationToken(
			principal,
			"password",
			List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
		);

		given(feignClient.getAllPublishers(0, 10))
			.willReturn(ResponseEntity.ok(pageResponse));

		// when & then
		mockMvc.perform(get("/admin/pub")
				.with(authentication(auth))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(view().name("admin/publisher/publisher"))
			.andExpect(model().attributeExists("publishers"))
			.andExpect(model().attributeExists("paging"));

		verify(feignClient).getAllPublishers(0, 10);
	}

	@Test
	@DisplayName("GET /admin/pub - 클라이언트 예외 발생 시 처리")
	void getPublishers_clientError_throwsException() throws Exception {
		// given
		given(feignClient.getAllPublishers(anyInt(), anyInt()))
			.willThrow(new RuntimeException("downstream error"));

		// when
		ServletException ex = assertThrows(ServletException.class, () ->
			mockMvc.perform(get("/admin/pub")
					.with(user("admin").roles("ADMIN")))
				.andReturn()
		);

		// then
		Throwable cause = ex.getCause();
		assertNotNull(cause);
		assertTrue(cause instanceof RuntimeException);
		assertEquals("downstream error", cause.getMessage());
	}

	// ───────────────────────── POST /admin/pub (생성) ─────────────────────────

	@Test
	@DisplayName("POST /admin/pub - 출판사 생성 성공 시 redirect")
	void createPublisher_createsPublisherAndRedirects() throws Exception {
		// given
		PublisherCreateRequest request = new PublisherCreateRequest("새출판사");

		// when & then
		mockMvc.perform(post("/admin/pub")
				.with(user("admin").roles("ADMIN"))
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/admin/pub"));

		verify(feignClient).createPublisher(any(PublisherCreateRequest.class));
	}

	@Test
	@DisplayName("POST /admin/pub - 출판사 생성 중 클라이언트 예외 발생 시 RuntimeException 래핑")
	void createPublisher_clientError_throwsException() throws Exception {
		// given
		PublisherCreateRequest request = new PublisherCreateRequest("새출판사");

		willThrow(new RuntimeException("create failed"))
			.given(feignClient).createPublisher(any(PublisherCreateRequest.class));

		// when
		ServletException ex = assertThrows(ServletException.class, () ->
			mockMvc.perform(post("/admin/pub")
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

	// ───────────────────────── POST /admin/pub/put/{publisher-id} (수정) ─────────────────────────

	@Test
	@DisplayName("POST /admin/pub/put/{publisherId} - 출판사 수정 성공")
	void updatePublisher_callsFeignClientAndReturnsOk() throws Exception {
		Long publisherId = 1L;
		PublisherUpdateRequest updateRequest = new PublisherUpdateRequest("수정된출판사");

		mockMvc.perform(post("/admin/pub/put/{publisher-id}", publisherId)
				.with(user("admin").roles("ADMIN"))
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateRequest)))
			.andExpect(status().isOk())
			.andExpect(content().string(""));

		verify(feignClient).updatePublisher(eq(publisherId), any(PublisherUpdateRequest.class));
	}

	@Test
	@DisplayName("POST /admin/pub/put/{publisherId} - 수정 중 클라이언트 예외 발생 시 RuntimeException 래핑")
	void updatePublisher_clientError_throwsException() throws Exception {
		Long publisherId = 1L;
		PublisherUpdateRequest updateRequest = new PublisherUpdateRequest("수정된출판사");

		willThrow(new RuntimeException("update failed"))
			.given(feignClient).updatePublisher(eq(publisherId), any(PublisherUpdateRequest.class));

		ServletException ex = assertThrows(ServletException.class, () ->
			mockMvc.perform(post("/admin/pub/put/{publisher-id}", publisherId)
					.with(user("admin").roles("ADMIN"))
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(updateRequest)))
				.andReturn()
		);

		Throwable cause = ex.getCause();
		assertNotNull(cause);
		assertTrue(cause instanceof RuntimeException);
		assertEquals("update failed", cause.getMessage());
	}

	// ───────────────────────── POST /admin/pub/delete/{publisher-id} (삭제) ─────────────────────────

	@Test
	@DisplayName("POST /admin/pub/delete/{publisherId} - 출판사 삭제 성공")
	void deletePublisher_callsFeignClientAndReturnsOk() throws Exception {
		Long publisherId = 1L;

		willDoNothing().given(feignClient).deletePublisher(publisherId);

		mockMvc.perform(post("/admin/pub/delete/{publisher-id}", publisherId)
				.with(user("admin").roles("ADMIN"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(content().string(""));

		verify(feignClient).deletePublisher(publisherId);
	}

	@Test
	@DisplayName("POST /admin/pub/delete/{publisherId} - 삭제 중 클라이언트 예외 발생 시 RuntimeException 래핑")
	void deletePublisher_clientError_throwsException() throws Exception {
		Long publisherId = 1L;

		willThrow(new RuntimeException("delete failed"))
			.given(feignClient).deletePublisher(eq(publisherId));

		ServletException ex = assertThrows(ServletException.class, () ->
			mockMvc.perform(post("/admin/pub/delete/{publisher-id}", publisherId)
					.with(user("admin").roles("ADMIN"))
					.with(csrf()))
				.andReturn()
		);

		assertThat(ex.getCause())
			.isInstanceOf(RuntimeException.class)
			.hasMessage("delete failed");
	}
}
