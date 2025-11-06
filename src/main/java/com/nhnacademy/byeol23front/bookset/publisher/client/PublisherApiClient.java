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
	@PostMapping(value = "/api/publishers")
	ResponseEntity<PublisherCreateResponse> createPublisher(@RequestBody PublisherCreateRequest request);

	@GetMapping(value = "/api/publishers")
	ResponseEntity<PageResponse<AllPublishersInfoResponse>> getAllPublishers(
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "5") int size
	);

	@DeleteMapping(value = "/api/publishers/{publisherId}")
	void deletePublisher(@PathVariable Long publisherId);

	@PutMapping(value = "/api/publishers/{publisherId}")
	PublisherUpdateResponse updatePublisher(@PathVariable Long publisherId, @RequestBody PublisherUpdateRequest publisherName);
}
