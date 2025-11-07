package com.nhnacademy.byeol23front.bookset.tag.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.nhnacademy.byeol23front.bookset.tag.dto.AllTagsInfoResponse;
import com.nhnacademy.byeol23front.bookset.tag.dto.PageResponse;
import com.nhnacademy.byeol23front.bookset.tag.dto.TagCreateRequest;
import com.nhnacademy.byeol23front.bookset.tag.dto.TagCreateResponse;
import com.nhnacademy.byeol23front.bookset.tag.dto.TagUpdateRequest;
import com.nhnacademy.byeol23front.bookset.tag.dto.TagUpdateResponse;

@FeignClient(name = "BYEOL23-GATEWAY", contextId = "tagApiClient")
public interface TagApiClient {
	@PostMapping(value = "/api/tags")
	ResponseEntity<TagCreateResponse> createTag(@RequestBody TagCreateRequest request);

	@GetMapping(value = "/api/tags")
	ResponseEntity<PageResponse<AllTagsInfoResponse>> getAllTags(
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "5") int size
		);

	@DeleteMapping(value = "/api/tags/{tag-id}")
	void deleteTag(@PathVariable(name = "tag-id") Long tagId);

	@PutMapping(value = "/api/tags/{tag-id}")
	TagUpdateResponse updateTag(@PathVariable(name = "tag-id") Long tagId, @RequestBody TagUpdateRequest tagName);
}
