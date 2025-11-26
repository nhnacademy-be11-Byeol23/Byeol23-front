package com.nhnacademy.byeol23front.bookset.publisher.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.byeol23front.bookset.category.client.CategoryApiClient;
import com.nhnacademy.byeol23front.bookset.publisher.client.PublisherApiClient;
import com.nhnacademy.byeol23front.bookset.publisher.dto.AllPublishersInfoResponse;
import com.nhnacademy.byeol23front.bookset.publisher.dto.PageResponse;
import com.nhnacademy.byeol23front.bookset.publisher.dto.PublisherCreateRequest;
import com.nhnacademy.byeol23front.bookset.publisher.dto.PublisherUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PublisherController.class)
class PublisherControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockBean
	PublisherApiClient feignClient;

	// 👇 CategoryHeaderAdvice 때문에 필요했던 것처럼 여기서도 mock 해 줍니다.
	@MockBean
	CategoryApiClient categoryApiClient;

	// ───────────────────────────────── GET /admin/pub ─────────────────────────────────

	@Test
	@DisplayName("GET /admin/pub - 출판사 목록 페이지 렌더링")
	void getPublishers_returnsViewWithModel() throws Exception {
		// given
		AllPublishersInfoResponse publisher = new AllPublishersInfoResponse(
			1L,
			"NHN Publisher"   // 실제 record 정의에 맞게 수정
		);

		PageResponse<AllPublishersInfoResponse> pageResponse =
			new PageResponse<>(
				List.of(publisher), // content
				0,                  // page
				10,                 // size
				1L,                 // totalElements
				1,                  // totalPages
				true,               // first
				true                // last
			);

		given(feignClient.getAllPublishers(0, 10))
			.willReturn(ResponseEntity.ok(pageResponse));

		// when & then
		mockMvc.perform(get("/admin/pub")
				.with(user("admin").roles("ADMIN"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(view().name("admin/publisher/publisher"))
			.andExpect(model().attributeExists("publishers"))
			.andExpect(model().attributeExists("paging"));

		verify(feignClient).getAllPublishers(0, 10);
	}

	@Test
	@DisplayName("GET /admin/pub - 클라이언트 예외 발생 시 error 뷰")
	void getPublishers_clientError_returnsErrorView() throws Exception {
		// given
		given(feignClient.getAllPublishers(anyInt(), anyInt()))
			.willThrow(new RuntimeException("downstream error"));

		// when & then
		mockMvc.perform(get("/admin/pub")
				.with(user("admin").roles("ADMIN")))
			.andExpect(view().name("error"));
	}

	// ───────────────────────────────── POST /admin/pub (생성) ─────────────────────────────────

	@Test
	@DisplayName("POST /admin/pub - 출판사 생성 성공 시 redirect")
	void createPublisher_createsPublisherAndRedirects() throws Exception {
		// given
		PublisherCreateRequest request = new PublisherCreateRequest("NHN Publisher");
		// feignClient.createPublisher(...) 의 반환값은 컨트롤러에서 사용 안 하므로 굳이 stub 안 해도 됨

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
	@DisplayName("POST /admin/pub - 출판사 생성 중 클라이언트 예외 발생 시 error 뷰")
	void createPublisher_clientError_returnsErrorView() throws Exception {
		// given
		PublisherCreateRequest request = new PublisherCreateRequest("NHN Publisher");

		given(feignClient.createPublisher(any(PublisherCreateRequest.class)))
			.willThrow(new RuntimeException("create failed"));

		// when & then
		mockMvc.perform(post("/admin/pub")
				.with(user("admin").roles("ADMIN"))
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(view().name("error"));
	}

	// ───────────────────────────────── POST /admin/pub/delete/{id} ─────────────────────────────────

	@Test
	@DisplayName("POST /admin/pub/delete/{publisher-id} - 출판사 삭제 성공")
	void deletePublisher_callsFeignClientAndReturnsOk() throws Exception {
		// given
		Long publisherId = 1L;

		willDoNothing().given(feignClient).deletePublisher(publisherId); // void 라고 가정

		// when & then
		mockMvc.perform(post("/admin/pub/delete/{publisher-id}", publisherId)
				.with(user("admin").roles("ADMIN"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(content().string(""));

		verify(feignClient).deletePublisher(publisherId);
	}

	@Test
	@DisplayName("POST /admin/pub/delete/{publisher-id} - 삭제 중 클라이언트 예외 발생 시 error 뷰")
	void deletePublisher_clientError_returnsErrorView() throws Exception {
		// given
		Long publisherId = 1L;

		willThrow(new RuntimeException("delete failed"))
			.given(feignClient).deletePublisher(eq(publisherId));

		// when & then
		mockMvc.perform(post("/admin/pub/delete/{publisher-id}", publisherId)
				.with(user("admin").roles("ADMIN"))
				.with(csrf()))
			.andExpect(view().name("error"));
	}

	// ───────────────────────────────── POST /admin/pub/put/{id} ─────────────────────────────────

	@Test
	@DisplayName("POST /admin/pub/put/{publisher-id} - 출판사 수정 성공")
	void updatePublisher_callsFeignClientAndReturnsOk() throws Exception {
		Long publisherId = 1L;
		PublisherUpdateRequest updateRequest = new PublisherUpdateRequest("New Name");

		// feignClient.updatePublisher(...) 반환 값은 컨트롤러에서 사용 안 함 → stub 불필요

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
	@DisplayName("POST /admin/pub/put/{publisher-id} - 수정 중 클라이언트 예외 발생 시 5xx")
	void updatePublisher_clientError_returns5xx() throws Exception {
		// given
		Long publisherId = 1L;
		PublisherUpdateRequest updateRequest = new PublisherUpdateRequest("New Name");

		given(feignClient.updatePublisher(eq(publisherId), any(PublisherUpdateRequest.class)))
			.willThrow(new RuntimeException("update failed"));

		// when & then
		mockMvc.perform(post("/admin/pub/put/{publisher-id}", publisherId)
				.with(user("admin").roles("ADMIN"))
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateRequest)))
			.andExpect(status().is5xxServerError());
	}
}
