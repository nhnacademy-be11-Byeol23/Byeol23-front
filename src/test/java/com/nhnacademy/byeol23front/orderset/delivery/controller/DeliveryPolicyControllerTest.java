package com.nhnacademy.byeol23front.orderset.delivery.controller;

import com.fasterxml.jackson.databind.ObjectMapper; // [추가] ObjectMapper 임포트
import com.nhnacademy.byeol23front.auth.AuthHelper;
import com.nhnacademy.byeol23front.bookset.category.client.CategoryApiClient;
import com.nhnacademy.byeol23front.orderset.delivery.client.DeliveryApiClient;
import com.nhnacademy.byeol23front.orderset.delivery.dto.DeliveryPolicyCreateRequest;
import com.nhnacademy.byeol23front.orderset.delivery.dto.DeliveryPolicyCreateResponse;
import com.nhnacademy.byeol23front.orderset.delivery.dto.DeliveryPolicyInfoResponse;
// [제거] JwtParser, Claims, Member, MemberRepository 임포트 제거
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
// [제거] Mockito 임포트 제거 (필요시 'any' 등만 남김)
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DeliveryPolicyController.class)
@Import(AuthHelper.class)
class DeliveryPolicyControllerTest { // 클래스 이름 수정 (Test 접미어)

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private DeliveryApiClient deliveryApiClient;

	@MockBean
	private CategoryApiClient categoryApiClient;

	@MockBean
	private AuthHelper authHelper;

	private Pageable defaultPageable;
	private DeliveryPolicyInfoResponse infoResponse;

	// [추가] ObjectMapper가 주입되지 않았다면 Autowired로 추가
	@Autowired
	private ObjectMapper objectMapper;

	@BeforeEach
	void setUp() {

		// 테스트용 DTO 및 Pageable 초기화
		defaultPageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "changedAt"));

		infoResponse = new DeliveryPolicyInfoResponse(
			new BigDecimal("50000"),
			new BigDecimal("3000"),
			LocalDateTime.now()
		);
	}

	@Test
	@DisplayName("GET /admin/policies/deliveries - 배송 정책 목록 조회 성공")
	void getDeliveryMain_Success() throws Exception {
		// given
		Page<DeliveryPolicyInfoResponse> mockPage = new PageImpl<>(List.of(infoResponse), defaultPageable, 1);
		ResponseEntity<Page<DeliveryPolicyInfoResponse>> mockResponseEntity = ResponseEntity.ok(mockPage);

		given(deliveryApiClient.getDeliveryPolicies(any(Pageable.class))).willReturn(mockResponseEntity);

		// when & then
		mockMvc.perform(get("/admin/policies/deliveries")
				.with(csrf())
				.with(user("admin").roles("ADMIN"))
				.param("page", "0")
				.param("size", "10")
				.param("sort", "changedAt,DESC"))
			.andExpect(status().isOk())
			.andExpect(view().name("admin/policy/delivery")) // 반환되는 뷰 이름 검증
			.andExpect(model().attributeExists("policies")) // "policies" 속성이 Model에 있는지 검증
			.andExpect(model().attribute("policies", mockPage));

		verify(deliveryApiClient, times(1)).getDeliveryPolicies(any(Pageable.class));
	}

	@Test
	@DisplayName("GET /admin/policies/deliveries - API 클라이언트 실패 시 error 뷰 반환")
	void getDeliveryMain_ApiFailure() throws Exception {
		// given
		// API 클라이언트가 400 Bad Request를 반환한다고 가정
		ResponseEntity<Page<DeliveryPolicyInfoResponse>> mockResponseEntity = ResponseEntity.badRequest().build();
		given(deliveryApiClient.getDeliveryPolicies(any(Pageable.class))).willReturn(mockResponseEntity);

		// when & then
		mockMvc.perform(get("/admin/policies/deliveries")
				.with(csrf())
				.with(user("admin").roles("ADMIN"))
				.param("page", "0")
				.param("size", "10")
				.param("sort", "changedAt,DESC"))
			.andExpect(status().isOk()) // 컨트롤러 자체는 200 OK
			.andExpect(view().name("error")) // "error" 뷰를 반환
			.andExpect(model().attributeExists("error")) // Model에 "error" 속성이 있는지
			.andExpect(model().attribute("status", 400));
	}

	@Test
	@DisplayName("POST /admin/policies/deliveries - 배송 정책 생성 성공")
	void createDeliveryPolicy_Success() throws Exception {
		// given
		DeliveryPolicyCreateResponse createResponse = new DeliveryPolicyCreateResponse(
			2L, new BigDecimal("2500"), new BigDecimal("40000"), LocalDateTime.now()
		);
		ResponseEntity<DeliveryPolicyCreateResponse> mockResponseEntity = ResponseEntity.created(null).body(createResponse);

		// ArgumentCaptor: 컨트롤러가 DTO를 올바르게 생성해서 넘겼는지 확인
		ArgumentCaptor<DeliveryPolicyCreateRequest> requestCaptor = ArgumentCaptor.forClass(DeliveryPolicyCreateRequest.class);

		given(deliveryApiClient.createDeliveryPolicy(requestCaptor.capture())).willReturn(mockResponseEntity);

		// when & then
		mockMvc.perform(post("/admin/policies/deliveries")
				.with(csrf())
				.with(user("admin").roles("ADMIN"))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED) // 폼 제출
				.param("deliveryFee", "2500")
				.param("freeDeliveryCondition", "40000"))
			.andExpect(status().is3xxRedirection()) // 302 Redirect
			.andExpect(redirectedUrl("/admin/policies/deliveries")); // 리다렉트 URL 검증

		// verify
		// Feign Client가 1번 호출되었는지
		verify(deliveryApiClient, times(1)).createDeliveryPolicy(any(DeliveryPolicyCreateRequest.class));
		// DTO에 폼 데이터가 올바르게 바인딩되었는지 검증
		assertThat(requestCaptor.getValue().deliveryFee()).isEqualByComparingTo("2500");
		assertThat(requestCaptor.getValue().freeDeliveryCondition()).isEqualByComparingTo("40000");
	}

	@Test
	@DisplayName("POST /admin/policies/deliveries - API 클라이언트 실패 시 error 뷰 반환")
	void createDeliveryPolicy_ApiFailure() throws Exception {
		// given
		// API 클라이언트가 500 Internal Server Error를 반환한다고 가정
		ResponseEntity<DeliveryPolicyCreateResponse> mockResponseEntity = ResponseEntity.internalServerError().build();
		given(deliveryApiClient.createDeliveryPolicy(any(DeliveryPolicyCreateRequest.class))).willReturn(mockResponseEntity);

		// when & then
		mockMvc.perform(post("/admin/policies/deliveries")
				.with(csrf())
				.with(user("admin").roles("ADMIN"))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("deliveryFee", "2500")
				.param("freeDeliveryCondition", "40000"))
			.andExpect(status().isOk())
			.andExpect(view().name("error"))
			.andExpect(model().attributeExists("message"));
	}
}