package com.nhnacademy.byeol23front.bookset.contributor.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.nhnacademy.byeol23front.bookset.contributor.dto.AllContributorResponse;
import com.nhnacademy.byeol23front.bookset.contributor.dto.ContributorCreateRequest;
import com.nhnacademy.byeol23front.bookset.contributor.dto.ContributorCreateResponse;
import com.nhnacademy.byeol23front.bookset.contributor.dto.ContributorUpdateRequest;
import com.nhnacademy.byeol23front.bookset.contributor.dto.ContributorUpdateResponse;
import com.nhnacademy.byeol23front.bookset.contributor.dto.PageResponse;

@FeignClient(name = "BYEOL23-GATEWAY", contextId = "contributorApiClient")
public interface ContributorApiClient {

	@GetMapping(value = "/api/contributors")
	ResponseEntity<PageResponse<AllContributorResponse>> getAllContributors(@RequestParam(value = "page") int page, @RequestParam(value = "size") int size);

	@PostMapping(value = "/api/contributors")
	ResponseEntity<ContributorCreateResponse> createContributor(@RequestBody ContributorCreateRequest contributorCreateRequest);

	@PutMapping(value = "/api/contributors/{contributor-id}")
	ContributorUpdateResponse updateContributor(@PathVariable(name = "contributor-id") Long contributorId, @RequestBody ContributorUpdateRequest contributorUpdateRequest);

	@DeleteMapping(value = "/api/contributors/{contributor-id}")
	void deleteContributor(@PathVariable(name = "contributor-id") Long contributorId);
}
