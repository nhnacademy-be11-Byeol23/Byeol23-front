package com.nhnacademy.byeol23front.orderset.packaging.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.nhnacademy.byeol23front.minio.dto.back.GetUrlResponse;
import com.nhnacademy.byeol23front.minio.service.MinioService;
import com.nhnacademy.byeol23front.minio.util.ImageDomain;
import com.nhnacademy.byeol23front.orderset.packaging.client.PackagingApiClient;
import com.nhnacademy.byeol23front.orderset.packaging.dto.PackagingCreateRequest;
import com.nhnacademy.byeol23front.orderset.packaging.dto.PackagingCreateResponse;
import com.nhnacademy.byeol23front.orderset.packaging.dto.PackagingCreateTmpRequest;
import com.nhnacademy.byeol23front.orderset.packaging.dto.PackagingInfoResponse;
import com.nhnacademy.byeol23front.orderset.packaging.dto.PackagingUpdateRequest;
import com.nhnacademy.byeol23front.orderset.packaging.dto.PackagingUpdateResponse;
import com.nhnacademy.byeol23front.orderset.packaging.dto.PackagingUpdateTmpRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/admin/packagings")
@RequiredArgsConstructor
public class PackagingController {
	private final PackagingApiClient packagingApiClient;
	private final MinioService minioService;

	@GetMapping
	public String getPackagingMain(@PageableDefault(size = 10) Pageable pageable, Model model) {
		ResponseEntity<Page<PackagingInfoResponse>> responses = packagingApiClient.getAllPackagings(pageable);

		model.addAttribute("packaging", responses.getBody());

		return "admin/book/packaging";
	}

	@PostMapping
	public String createPackaging(@ModelAttribute PackagingCreateTmpRequest tmp, Model model) {

		PackagingCreateRequest request = new PackagingCreateRequest(tmp.packagingName(), tmp.packagingPrice());

		ResponseEntity<PackagingCreateResponse> response = packagingApiClient.createPackaging(request);

		minioService.uploadImage(ImageDomain.PACKAGING, response.getBody().packagingId(), tmp.imageFile());

		if(!response.getStatusCode().is2xxSuccessful()) {
			model.addAttribute("status", 400); // 400 Bad Request
			model.addAttribute("error", "포장지 저장 실패");
			model.addAttribute("message", "포장지 저장에 실패했습니니다.");
			return "error";
		}

		return "redirect:/admin/packagings";
	}

	@PostMapping("/{packaging-id}") //
	public String updatePackaging(@PathVariable(name = "packaging-id") Long packagingId, @ModelAttribute PackagingUpdateTmpRequest tmp) {
		if (tmp.imageFile() != null && !tmp.imageFile().isEmpty()) {
			List<GetUrlResponse> res = minioService.getImageUrl(ImageDomain.PACKAGING, packagingId);
			minioService.deleteImage(ImageDomain.PACKAGING, res.getFirst().imageId());
			minioService.uploadImage(ImageDomain.PACKAGING, packagingId, tmp.imageFile());
		}

		PackagingUpdateRequest request = new PackagingUpdateRequest(tmp.packagingName(), tmp.packagingPrice());

		packagingApiClient.updatePackaging(packagingId, request);

		return "redirect:/admin/packagings";
	}

	@PostMapping("/{packaging-id}/delete")
	@ResponseBody
	public ResponseEntity<Void> deletePackagingById(@PathVariable(name = "packaging-id") Long packagingId) {
		minioService.deleteImage(ImageDomain.PACKAGING, packagingId);
		packagingApiClient.deleteById(packagingId);

		return ResponseEntity.noContent().build();
	}

}
