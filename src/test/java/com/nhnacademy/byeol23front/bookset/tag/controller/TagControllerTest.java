package com.nhnacademy.byeol23front.bookset.tag.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.byeol23front.auth.AuthUtil;
import com.nhnacademy.byeol23front.bookset.category.client.CategoryApiClient;
import com.nhnacademy.byeol23front.bookset.tag.client.TagApiClient;
import com.nhnacademy.byeol23front.bookset.tag.dto.AllTagsInfoResponse;
import com.nhnacademy.byeol23front.bookset.tag.dto.PageResponse;
import com.nhnacademy.byeol23front.bookset.tag.dto.TagCreateRequest;
import com.nhnacademy.byeol23front.bookset.tag.dto.TagCreateResponse;
import com.nhnacademy.byeol23front.bookset.tag.dto.TagUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import jakarta.servlet.ServletException;

@WebMvcTest(TagController.class)
class TagControllerTest {

	// ───────────── Test principal for Thymeleaf header (sec:authentication="principal.nickname") ─────────────
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
	TagApiClient feignClient;

	// common header / advice 에서 사용
	@MockBean
	CategoryApiClient categoryApiClient;

	@MockBean(name = "authHelper")
    AuthUtil authUtil;

	// ───────────────────────── GET /admin/tags ─────────────────────────

	@Test
	@DisplayName("GET /admin/tags - 태그 목록 페이지 렌더링")
	void getTags_returnsViewWithModel() throws Exception {
		// given
		given(authUtil.isLoggedIn()).willReturn(true);

		AllTagsInfoResponse tag = new AllTagsInfoResponse(
			1L,
			"공포"
		);

		PageResponse<AllTagsInfoResponse> pageResponse =
			new PageResponse<>(
				List.of(tag), // content
				0,            // number (page index)
				10,           // size  (page size)
				1L,           // totalElements
				1,            // totalPages
				true,         // first
				true          // last
			);

		// principal with nickname for Thymeleaf header
		TestPrincipal principal = new TestPrincipal("admin", "관리자닉");
		Authentication auth = new UsernamePasswordAuthenticationToken(
			principal,
			"password",
			List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
		);

		given(feignClient.getAllTags(0, 10))
			.willReturn(ResponseEntity.ok(pageResponse));

		// when & then
		mockMvc.perform(get("/admin/tags")
				.with(authentication(auth))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(view().name("admin/tags/tag"))
			.andExpect(model().attributeExists("tags"))
			.andExpect(model().attributeExists("paging"));

		verify(feignClient).getAllTags(0, 10);
	}

	@Test
	@DisplayName("GET /admin/tags - 클라이언트 예외 발생 시 처리")
	void getTags_clientError_throwsException() throws Exception {
		// given
		given(authUtil.isLoggedIn()).willReturn(true);

		TestPrincipal principal = new TestPrincipal("admin", "관리자닉");
		Authentication auth = new UsernamePasswordAuthenticationToken(
			principal,
			"password",
			List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
		);

		given(feignClient.getAllTags(anyInt(), anyInt()))
			.willThrow(new RuntimeException("downstream error"));

		// when
		ServletException ex = assertThrows(ServletException.class, () ->
			mockMvc.perform(get("/admin/tags")
					.with(authentication(auth))
					.with(csrf()))
				.andReturn()
		);

		// then
		Throwable cause = ex.getCause();
		assertNotNull(cause);
		assertTrue(cause instanceof RuntimeException);
		assertEquals("downstream error", cause.getMessage());
	}

	// ───────────────────────── POST /admin/tags (생성) ─────────────────────────

	@Test
	@DisplayName("POST /admin/tags - 태그 생성 성공 시 200 + body")
	void createTag_createsTagAndReturnsBody() throws Exception {
		TagCreateRequest request = new TagCreateRequest("공포");

		TagCreateResponse created = new TagCreateResponse();

		given(feignClient.createTag(any(TagCreateRequest.class)))
			.willReturn(ResponseEntity.ok(created));

		// when & then
		mockMvc.perform(post("/admin/tags")
				.with(user("admin").roles("ADMIN"))
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk());
		// 필요하면 여기서 jsonPath 로 응답 body 검증도 가능

		verify(feignClient).createTag(any(TagCreateRequest.class));
	}

	@Test
	@DisplayName("POST /admin/tags - 태그 생성 중 클라이언트 예외 발생 시 RuntimeException")
	void createTag_clientError_throwsException() throws Exception {
		// given
		TagCreateRequest request = new TagCreateRequest("공포");

		given(feignClient.createTag(any(TagCreateRequest.class)))
			.willThrow(new RuntimeException("create failed"));

		// when
		ServletException ex = assertThrows(ServletException.class, () ->
			mockMvc.perform(post("/admin/tags")
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

	// ───────────────────────── POST /admin/tags/put/{tag-id} (수정) ─────────────────────────

	@Test
	@DisplayName("POST /admin/tags/put/{tagId} - 태그 수정 성공")
	void updateTag_callsFeignClientAndReturnsOk() throws Exception {
		Long tagId = 1L;

		TagUpdateRequest updateRequest = new TagUpdateRequest("새 태그");

		// 컨트롤러는 반환값 사용 X → stub 불필요 (기본 null 반환이지만, 우리는 예외만 안 나면 됨)
		mockMvc.perform(post("/admin/tags/put/{tag-id}", tagId)
				.with(user("admin").roles("ADMIN"))
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateRequest)))
			.andExpect(status().isOk())
			.andExpect(content().string(""));

		verify(feignClient).updateTag(eq(tagId), any(TagUpdateRequest.class));
	}

	@Test
	@DisplayName("POST /admin/tags/put/{tagId} - 수정 중 클라이언트 예외 발생 시 RuntimeException")
	void updateTag_clientError_throwsException() throws Exception {
		Long tagId = 1L;
		TagUpdateRequest updateRequest = new TagUpdateRequest("새 태그");

		given(feignClient.updateTag(eq(tagId), any(TagUpdateRequest.class)))
			.willThrow(new RuntimeException("update failed"));

		ServletException ex = assertThrows(ServletException.class, () ->
			mockMvc.perform(post("/admin/tags/put/{tag-id}", tagId)
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

	// ───────────────────────── POST /admin/tags/delete/{tag-id} (삭제) ─────────────────────────

	@Test
	@DisplayName("POST /admin/tags/delete/{tagId} - 태그 삭제 성공")
	void deleteTag_callsFeignClientAndReturnsOk() throws Exception {
		Long tagId = 1L;

		willDoNothing().given(feignClient).deleteTag(tagId);

		mockMvc.perform(post("/admin/tags/delete/{tag-id}", tagId)
				.with(user("admin").roles("ADMIN"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(content().string(""));

		verify(feignClient).deleteTag(tagId);
	}

	@Test
	@DisplayName("POST /admin/tags/delete/{tagId} - 삭제 중 클라이언트 예외 발생 시 RuntimeException")
	void deleteTag_clientError_throwsException() throws Exception {
		Long tagId = 1L;

		willThrow(new RuntimeException("delete failed"))
			.given(feignClient).deleteTag(eq(tagId));

		ServletException ex = assertThrows(ServletException.class, () ->
			mockMvc.perform(post("/admin/tags/delete/{tag-id}", tagId)
					.with(user("admin").roles("ADMIN"))
					.with(csrf()))
				.andReturn()
		);

		Throwable cause = ex.getCause();
		assertNotNull(cause);
		assertTrue(cause instanceof RuntimeException);
		assertEquals("delete failed", cause.getMessage());
	}
}
