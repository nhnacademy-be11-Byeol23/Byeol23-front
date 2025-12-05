package com.nhnacademy.byeol23front.orderset.packaging.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.nhnacademy.byeol23front.orderset.packaging.dto.PackagingCreateRequest;
import com.nhnacademy.byeol23front.orderset.packaging.dto.PackagingCreateResponse;
import com.nhnacademy.byeol23front.orderset.packaging.dto.PackagingInfoResponse;
import com.nhnacademy.byeol23front.orderset.packaging.dto.PackagingUpdateRequest;
import com.nhnacademy.byeol23front.orderset.packaging.dto.PackagingUpdateResponse;


@FeignClient(name = "BYEOL23-GATEWAY", contextId = "packagingApiClient")
public interface PackagingApiClient {

	@GetMapping("/api/packagings")
	ResponseEntity<Page<PackagingInfoResponse>> getAllPackagings(Pageable pageable);

	@PostMapping("/api/packagings")
	ResponseEntity<PackagingCreateResponse> createPackaging(@RequestBody PackagingCreateRequest request);

	@PostMapping("/api/packagings/{packaging-id}/update")
	ResponseEntity<PackagingUpdateResponse> updatePackaging(@PathVariable(name = "packaging-id") Long packagingId, @RequestBody PackagingUpdateRequest request);

	@PostMapping("/api/packagings/{packaging-id}/delete")
	ResponseEntity<Void> deleteById(@PathVariable(name = "packaging-id") Long packagingId);

	@GetMapping("/api/packagings/lists")
	List<PackagingInfoResponse> getAllPackagingLists();

}
