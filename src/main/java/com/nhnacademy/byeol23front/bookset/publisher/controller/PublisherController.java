package com.nhnacademy.byeol23front.bookset.publisher.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nhnacademy.byeol23front.bookset.publisher.client.PublisherApiClient;
import com.nhnacademy.byeol23front.bookset.publisher.dto.AllPublishersInfoResponse;
import com.nhnacademy.byeol23front.bookset.publisher.dto.PageResponse;
import com.nhnacademy.byeol23front.bookset.publisher.dto.PublisherCreateRequest;
import com.nhnacademy.byeol23front.bookset.publisher.dto.PublisherUpdateRequest;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/publishers")
public class PublisherController {
	private final PublisherApiClient feignClient;

	@GetMapping
	public String getPublishers(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		Model model
		){
		PageResponse<AllPublishersInfoResponse> response = feignClient.getAllPublishers(page, size).getBody();
		model.addAttribute("publishers", response.content());
		model.addAttribute("paging", response);
		return "admin/publisher/publisher";
	}

	@PostMapping
	public String createPublisher(@RequestBody PublisherCreateRequest request){
		feignClient.createPublisher(request);
		return "redirect:/admin/publishers";
	}

	@ResponseBody
	@DeleteMapping("/{publisher-id}")
	public ResponseEntity<Void> deletePublisher(@PathVariable(name = "publisher-id") Long publisherId){
		feignClient.deletePublisher(publisherId);
		return ResponseEntity.ok().build();
	}

	@ResponseBody
	@PutMapping("/{publisher-id}")
	public ResponseEntity<Void> updatePublisher(@PathVariable(name = "publisher-id") Long publisherId, @RequestBody PublisherUpdateRequest publisherName){
		feignClient.updatePublisher(publisherId, publisherName);
		return ResponseEntity.ok().build();
	}
}
