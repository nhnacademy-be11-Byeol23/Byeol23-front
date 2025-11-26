package com.nhnacademy.byeol23front.bookset.tag.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.byeol23front.bookset.category.client.CategoryApiClient;
import com.nhnacademy.byeol23front.bookset.tag.client.TagApiClient;
import com.nhnacademy.byeol23front.bookset.tag.dto.AllTagsInfoResponse;
import com.nhnacademy.byeol23front.bookset.tag.dto.PageResponse;
import com.nhnacademy.byeol23front.bookset.tag.dto.TagCreateRequest;
import com.nhnacademy.byeol23front.bookset.tag.dto.TagCreateResponse;
import com.nhnacademy.byeol23front.bookset.tag.dto.TagUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TagController.class)
class TagControllerTest {

	@Autowired
	MockMvc mockMvc;

	@MockBean
	TagApiClient feignClient;

	@MockBean
	private CategoryApiClient categoryApiClient;

	@Autowired
	ObjectMapper objectMapper;

	// @Test
	// @DisplayName("GET /admin/tags - 태그 목록 페이지 렌더링")
	// void getTags_returnsViewWithModel() throws Exception {
	// 	// given
	// 	AllTagsInfoResponse tag = new AllTagsInfoResponse(
	// 		1L,
	// 		"backend"
	// 		// 필요하면 필드 더 추가
	// 	);
	//
	// 	// ⚠️ PageResponse 생성자는 실제 정의에 맞게 수정하세요.
	// 	PageResponse<AllTagsInfoResponse> pageResponse =
	// 		new PageResponse<>(
	// 			List.of(tag), // content
	// 			0,            // page
	// 			10,           // size
	// 			1L,           // totalElements
	// 			1,            // totalPages
	// 			true,         // first
	// 			true          // last
	// 		);
	//
	// 	given(feignClient.getAllTags(0, 10))
	// 		.willReturn(ResponseEntity.ok(pageResponse));
	//
	// 	// when & then
	// 	mockMvc.perform(get("/admin/tags")
	// 			.with(user("admin").roles("ADMIN"))
	// 			.with(csrf()))
	// 		.andExpect(status().isOk())
	// 		.andExpect(view().name("admin/tags/tag"))
	// 		.andExpect(model().attributeExists("tags"))
	// 		.andExpect(model().attributeExists("paging"));
	//
	// 	verify(feignClient).getAllTags(0, 10);
	// }

	@Test
	@DisplayName("POST /admin/tags - 태그 생성")
	void createTag_createsTagAndReturnsResponseBody() throws Exception {
		// given
		TagCreateRequest request = new TagCreateRequest("backend"); // 생성자 형태 맞게 수정
		TagCreateResponse response = new TagCreateResponse(
		);

		given(feignClient.createTag(any(TagCreateRequest.class)))
			.willReturn(ResponseEntity.ok(response));

		// when & then
		mockMvc.perform(post("/admin/tags")
				.with(user("admin").roles("ADMIN"))
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(content().json(objectMapper.writeValueAsString(response)));

		verify(feignClient).createTag(any(TagCreateRequest.class));
	}

	@Test
	@DisplayName("POST /admin/tags/delete/{tag-id} - 태그 삭제")
	void deleteTag_callsFeignClientAndReturnsOk() throws Exception {
		// given
		Long tagId = 1L;

		willDoNothing().given(feignClient).deleteTag(tagId);

		// when & then
		mockMvc.perform(post("/admin/tags/delete/{tag-id}", tagId)
				.with(user("admin").roles("ADMIN"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(content().string("")); // ResponseEntity<Void>

		verify(feignClient).deleteTag(tagId);
	}

	@Test
	@DisplayName("POST /admin/tags/put/{tag-id} - 태그 수정")
	void updateTag_callsFeignClientAndReturnsOk() throws Exception {
		Long tagId = 1L;
		TagUpdateRequest updateRequest = new TagUpdateRequest("new-name");

		// 👉 no willDoNothing / no given() at all

		mockMvc.perform(post("/admin/tags/put/{tag-id}", tagId)
				.with(csrf())
				.with(user("admin").roles("ADMIN"))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateRequest)))
			.andExpect(status().isOk())
			.andExpect(content().string(""));

		// just verify it was called
		verify(feignClient).updateTag(eq(tagId), any(TagUpdateRequest.class));
	}

	// @Test
	// @DisplayName("GET /admin/tags - 클라이언트 예외 발생 시 5xx")
	// void getTags_clientError_returns5xx() throws Exception {
	// 	// given
	// 	given(feignClient.getAllTags(anyInt(), anyInt()))
	// 		.willThrow(new RuntimeException("downstream error"));
	//
	// 	// when & then
	// 	mockMvc.perform(get("/admin/tags")
	// 			.with(user("admin").roles("ADMIN")))
	// 		.andExpect(view().name("error"));
	// }

	// @Test
	// @DisplayName("POST /admin/tags - 태그 생성 중 클라이언트 예외 발생 시 5xx")
	// void createTag_clientError_returns5xx() throws Exception {
	// 	// given
	// 	TagCreateRequest request = new TagCreateRequest("backend");
	//
	// 	given(feignClient.createTag(any(TagCreateRequest.class)))
	// 		.willThrow(new RuntimeException("create failed"));
	//
	// 	// when & then
	// 	mockMvc.perform(post("/admin/tags")
	// 			.with(user("admin").roles("ADMIN"))
	// 			.with(csrf())
	// 			.contentType(MediaType.APPLICATION_JSON)
	// 			.content(objectMapper.writeValueAsString(request)))
	// 		.andExpect(view().name("error"));
	// }

	// @Test
	// @DisplayName("POST /admin/tags/delete/{tag-id} - 삭제 중 클라이언트 예외 발생 시 5xx")
	// void deleteTag_clientError_returns5xx() throws Exception {
	// 	// given
	// 	Long tagId = 1L;
	//
	// 	willThrow(new RuntimeException("delete failed"))
	// 		.given(feignClient).deleteTag(eq(tagId));
	//
	// 	// when & then
	// 	mockMvc.perform(post("/admin/tags/delete/{tag-id}", tagId)
	// 			.with(user("admin").roles("ADMIN"))
	// 			.with(csrf()))
	// 		.andExpect(view().name("error"));
	// }

	// @Test
	// @DisplayName("POST /admin/tags/put/{tag-id} - 수정 중 클라이언트 예외 발생 시 5xx")
	// void updateTag_clientError_returns5xx() throws Exception {
	// 	// given
	// 	Long tagId = 1L;
	// 	TagUpdateRequest updateRequest = new TagUpdateRequest("new-name");
	//
	// 	given(feignClient.updateTag(eq(tagId), any(TagUpdateRequest.class)))
	// 		.willThrow(new RuntimeException("update failed"));
	//
	// 	// when & then
	// 	mockMvc.perform(post("/admin/tags/put/{tag-id}", tagId)
	// 			.with(user("admin").roles("ADMIN"))
	// 			.with(csrf())
	// 			.contentType(MediaType.APPLICATION_JSON)
	// 			.content(objectMapper.writeValueAsString(updateRequest)))
	// 		.andExpect(status().is5xxServerError());
	// }


}
