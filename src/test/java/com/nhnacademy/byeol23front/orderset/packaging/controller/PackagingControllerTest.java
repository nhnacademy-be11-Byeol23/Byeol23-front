package com.nhnacademy.byeol23front.orderset.packaging.controller;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.nhnacademy.byeol23front.bookset.category.client.CategoryApiClient;
import com.nhnacademy.byeol23front.minio.dto.back.GetUrlResponse;
import com.nhnacademy.byeol23front.minio.service.MinioService;
import com.nhnacademy.byeol23front.minio.util.ImageDomain;
import com.nhnacademy.byeol23front.orderset.packaging.client.PackagingApiClient;
import com.nhnacademy.byeol23front.orderset.packaging.dto.PackagingCreateRequest;
import com.nhnacademy.byeol23front.orderset.packaging.dto.PackagingCreateResponse;
import com.nhnacademy.byeol23front.orderset.packaging.dto.PackagingInfoResponse;
import com.nhnacademy.byeol23front.orderset.packaging.dto.PackagingUpdateRequest;

// PackagingController를 단위 테스트하기 위해 @WebMvcTest 사용
@WebMvcTest(PackagingController.class)
class PackagingControllerTest {

	@Autowired
	private MockMvc mockMvc;

	// 컨트롤러가 의존하는 빈들을 @MockBean으로 등록
	@MockBean
	private PackagingApiClient packagingApiClient;

	@MockitoBean
	private CategoryApiClient categoryApiClient;

	@MockBean
	private MinioService minioService;

	@Nested
	@DisplayName("포장지 목록 조회 (GET /admin/packagings)")
	class GetPackagingMainTests {

		@Test
		@DisplayName("포장지 목록 조회 성공")
		void testGetPackagingMainSuccess() throws Exception {
			// Given
			PackagingInfoResponse responseDto = new PackagingInfoResponse(1L, "테스트 포장지", new BigDecimal(3000),
				"http://testimage");
			Pageable pageable = PageRequest.of(0, 10);
			Page<PackagingInfoResponse> page = new PageImpl<>(List.of(responseDto), pageable, 1);

			// API 클라이언트가 정상 응답을 반환하도록 설정
			when(packagingApiClient.getAllPackagings(any(Pageable.class)))
				.thenReturn(ResponseEntity.ok(page));

			// When & Then
			mockMvc.perform(get("/admin/packagings")
					.with(csrf())
					.with(user("admin").roles("ADMIN"))
					.param("page", "0")
					.param("size", "10"))
				.andExpect(status().isOk()) // 200 OK 상태 확인
				.andExpect(view().name("admin/book/packaging")) // 뷰 이름 확인
				.andExpect(model().attributeExists("packaging")) // 모델에 "packaging" 속성이 있는지 확인
				.andExpect(model().attribute("packaging", page)); // 모델 속성 값 확인
		}
	}

	@Nested
	@DisplayName("포장지 생성 (POST /admin/packagings)")
	class CreatePackagingTests {

		@Test
		@DisplayName("포장지 생성 성공")
		void testCreatePackagingSuccess() throws Exception {
			// Given
			MockMultipartFile imageFile = new MockMultipartFile(
				"imageFile", "test.jpg", "image/jpeg", "test-image".getBytes()
			);

			// 테스트 입력값 정의
			Long expectedId = 1L;
			String inputName = "새 포장지";
			String inputPrice = "1500";

			// --- 수정된 부분 ---
			// 1. PackagingCreateResponse의 새 생성자에 맞게 모의 객체 생성
			// 컨트롤러 로직은 packagingId만 사용하므로, 다른 필드는
			// 테스트 일관성을 위해 입력값을 사용하거나, 사용되지 않는 필드(packagingImgUrl)는 null로 설정합니다.
			PackagingCreateResponse createResponse = new PackagingCreateResponse(
				expectedId,
				inputName,
				new BigDecimal(inputPrice),
				null // API 응답 DTO가 MultipartFile을 포함하는 것은 일반적이지 않으며,
				// 컨트롤러 로직에서 이 필드를 사용하지 않으므로 null로 설정합니다.
			);
			// --- 수정 끝 ---

			// API 클라이언트가 2xx 응답과 수정된 응답 본문을 반환하도록 설정
			when(packagingApiClient.createPackaging(any(PackagingCreateRequest.class)))
				.thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(createResponse));

			// When & Then
			mockMvc.perform(multipart("/admin/packagings")
					.file(imageFile)
					.param("packagingName", inputName) // @ModelAttribute 필드
					.param("packagingPrice", inputPrice) // @ModelAttribute 필드
					.with(csrf())
					.with(user("admin").roles("ADMIN"))
				.with(csrf())
				.with(user("admin").roles("ADMIN")))
				.andExpect(status().is3xxRedirection()) // 302 Redirect 상태 확인
				.andExpect(redirectedUrl("/admin/packagings")); // 리다이렉트 URL 확인
			// minioService.uploadImage가 응답받은 ID(expectedId)로 호출되었는지 검증
			verify(minioService, times(1)).uploadImage(ImageDomain.PACKAGING, expectedId, imageFile);
		}
		@Test
		@DisplayName("포장지 생성 실패 - API 클라이언트 400 반환")
		void testCreatePackagingApiFailure() throws Exception {
			// Given
			MockMultipartFile imageFile = new MockMultipartFile(
				"imageFile", "test.jpg", "image/jpeg", "test-image".getBytes()
			);

			// API 클라이언트가 400 Bad Request를 반환하도록 설정
			when(packagingApiClient.createPackaging(any(PackagingCreateRequest.class)))
				.thenReturn(ResponseEntity.badRequest().build());

			// When & Then
			mockMvc.perform(multipart("/admin/packagings")
					.file(imageFile)
					.param("packagingName", "새 포장지")
					.param("packagingPrice", "1500")
					.with(csrf())
					.with(user("admin").roles("ADMIN")))
				.andExpect(status().isOk()) // 에러 페이지로 forward되므로 200 OK
				.andExpect(view().name("error")) // "error" 뷰 이름 확인
				.andExpect(model().attribute("status", 400)) // 모델 속성 확인
				.andExpect(model().attribute("error", "포장지 저장 실패"));
			// API 호출이 실패했으므로 이미지 업로드는 호출되지 않아야 함
			verify(minioService, never()).uploadImage(any(), any(), any());
		}

		@Test
		@DisplayName("포장지 생성 실패 - API 2xx 응답이지만 Body가 null")
		void testCreatePackagingApiSuccessNullBody() throws Exception {
			// Given
			MockMultipartFile imageFile = new MockMultipartFile(
				"imageFile", "test.jpg", "image/jpeg", "test-image".getBytes()
			);

			// API 클라이언트가 200 OK는 반환하지만, body가 null인 비상 상황 설정
			when(packagingApiClient.createPackaging(any(PackagingCreateRequest.class)))
				.thenReturn(ResponseEntity.ok(null));

			// When & Then
			mockMvc.perform(multipart("/admin/packagings")
					.file(imageFile)
					.param("packagingName", "새 포장지")
					.param("packagingPrice", "1500")
					.with(csrf())
					.with(user("admin").roles("ADMIN")))
				.andExpect(status().isOk()) // 에러 페이지로 forward
				.andExpect(view().name("error"))
				.andExpect(model().attribute("status", 500))
				.andExpect(model().attribute("error", "API 응답 오류"));

			verify(minioService, never()).uploadImage(any(), any(), any());
		}
	}

	@Nested
	@DisplayName("포장지 수정 (POST /admin/packagings/{packaging-id})")
	class UpdatePackagingTests {

		private final Long packagingId = 1L;

		@Test
		@DisplayName("포장지 수정 성공 - 새 이미지 포함")
		void testUpdatePackagingWithNewImage() throws Exception {
			// Given
			MockMultipartFile newImageFile = new MockMultipartFile(
				"imageFile", "new.jpg", "image/jpeg", "new-image".getBytes()
			);

			// 기존 이미지 정보
			GetUrlResponse oldImageResponse = new GetUrlResponse(10L, "http://example.com/old.jpg");

			// Minio 서비스 모의 설정
			when(minioService.getImageUrl(ImageDomain.PACKAGING, packagingId))
				.thenReturn(List.of(oldImageResponse));

			// API 클라이언트 모의 설정 (컨트롤러에서 반환값을 사용하지 않으므로 ok()만)
			when(packagingApiClient.updatePackaging(eq(packagingId), any(PackagingUpdateRequest.class)))
				.thenReturn(ResponseEntity.ok().build()); // PackagingUpdateResponse가 없어도 됨

			// When & Then
			mockMvc.perform(multipart("/admin/packagings/" + packagingId)
					.file(newImageFile)
					.param("packagingName", "수정된 포장지")
					.param("packagingPrice", "2000")
					.with(csrf())
					.with(user("admin").roles("ADMIN")))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/admin/packagings"));

			// 기존 이미지 삭제 및 새 이미지 업로드 검증
			verify(minioService, times(1)).getImageUrl(ImageDomain.PACKAGING, packagingId);
			verify(minioService, times(1)).deleteImage(ImageDomain.PACKAGING, oldImageResponse.imageId());
			verify(minioService, times(1)).uploadImage(ImageDomain.PACKAGING, packagingId, newImageFile);
			verify(packagingApiClient, times(1)).updatePackaging(eq(packagingId), any(PackagingUpdateRequest.class));
		}

		@Test
		@DisplayName("포장지 수정 성공 - 이미지 변경 없음 (빈 파일)")
		void testUpdatePackagingWithoutImage() throws Exception {
			// Given
			// 빈 MultipartFile 시뮬레이션
			MockMultipartFile emptyImageFile = new MockMultipartFile(
				"imageFile", "", "application/octet-stream", new byte[0]
			);

			when(packagingApiClient.updatePackaging(eq(packagingId), any(PackagingUpdateRequest.class)))
				.thenReturn(ResponseEntity.ok().build());

			// When & Then
			mockMvc.perform(multipart("/admin/packagings/" + packagingId)
					.file(emptyImageFile) // 비어있는 파일 전송
					.param("packagingName", "수정된 포장지")
					.param("packagingPrice", "2000")
					.with(csrf())
					.with(user("admin").roles("ADMIN")))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/admin/packagings"));

			// 이미지가 비어있으므로 Minio 서비스는 호출되지 않아야 함
			verify(minioService, never()).getImageUrl(any(), any());
			verify(minioService, never()).deleteImage(any(), any());
			verify(minioService, never()).uploadImage(any(), any(), any());

			// API 클라이언트의 update는 호출되어야 함
			verify(packagingApiClient, times(1)).updatePackaging(eq(packagingId), any(PackagingUpdateRequest.class));
		}
	}

	@Nested
	@DisplayName("포장지 삭제 (POST /admin/packagings/{packaging-id}/delete)")
	class DeletePackagingTests {

		@Test
		@DisplayName("포장지 삭제 성공")
		void testDeletePackagingById() throws Exception {
			// Given
			Long packagingId = 1L;

			// API 클라이언트가 2xx 응답을 반환하도록 설정
			when(packagingApiClient.deleteById(packagingId))
				.thenReturn(ResponseEntity.noContent().build());

			// When & Then
			mockMvc.perform(post("/admin/packagings/" + packagingId + "/delete")
					.with(csrf())
					.with(user("admin").roles("ADMIN")))
				.andExpect(status().isNoContent()); // 204 No Content 확인

			// Minio 이미지 삭제와 API 삭제가 모두 호출되었는지 검증
			verify(minioService, times(1)).deleteImage(ImageDomain.PACKAGING, packagingId);
			verify(packagingApiClient, times(1)).deleteById(packagingId);
		}
	}
}