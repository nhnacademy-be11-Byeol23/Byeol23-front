package com.nhnacademy.byeol23front.bookset.publisher.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.nhnacademy.byeol23front.bookset.publisher.dto.AllPublishersInfoResponse;
import com.nhnacademy.byeol23front.bookset.publisher.dto.PageResponse;
import com.nhnacademy.byeol23front.bookset.publisher.dto.PublisherCreateRequest;
import com.nhnacademy.byeol23front.bookset.publisher.dto.PublisherCreateResponse;
import com.nhnacademy.byeol23front.bookset.publisher.dto.PublisherUpdateRequest;
import com.nhnacademy.byeol23front.bookset.publisher.dto.PublisherUpdateResponse;

@FeignClient(name = "BYEOL23-GATEWAY", contextId = "publisherApiClient")
public interface PublisherApiClient {
	@PostMapping(value = "/api/pub")
	ResponseEntity<PublisherCreateResponse> createPublisher(@RequestBody PublisherCreateRequest request);

	@GetMapping(value = "/api/pub")
	ResponseEntity<PageResponse<AllPublishersInfoResponse>> getAllPublishers(
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "10") int size
	);

	@GetMapping(value = "/api/pub/search")
	ResponseEntity<AllPublishersInfoResponse> findPublisherByName(@RequestParam String publisherName);

	@PostMapping(value = "/api/pub/find-or-create")
	ResponseEntity<AllPublishersInfoResponse> findOrCreatePublisher(@RequestBody PublisherCreateRequest request);

	@DeleteMapping(value = "/api/pub/{publisher-id}")
	void deletePublisher(@PathVariable(name = "publisher-id") Long publisherId);

	@PutMapping(value = "/api/pub/{publisher-id}")
	PublisherUpdateResponse updatePublisher(@PathVariable(name = "publisher-id") Long publisherId, @RequestBody PublisherUpdateRequest publisherName);
}
