package com.nhnacademy.byeol23front.bookset.contributor.controller;

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

import com.nhnacademy.byeol23front.bookset.contributor.client.ContributorApiClient;
import com.nhnacademy.byeol23front.bookset.contributor.dto.AllContributorResponse;
import com.nhnacademy.byeol23front.bookset.contributor.dto.ContributorCreateRequest;
import com.nhnacademy.byeol23front.bookset.contributor.dto.ContributorUpdateRequest;
import com.nhnacademy.byeol23front.bookset.contributor.dto.PageResponse;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/contributors")
@RequiredArgsConstructor
public class ContributorController {
	private final ContributorApiClient feignClient;

	@GetMapping
	public String getAllContributors(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "3") int size,
		Model model){

		PageResponse<AllContributorResponse> response = feignClient.getAllContributors(page, size).getBody();
		model.addAttribute("contributors", response.content());
		model.addAttribute("paging", response);
		return "admin/contributor/contributor";
	}

	@PostMapping
	public String createContributor(@RequestBody ContributorCreateRequest request, Model model){
		feignClient.createContributor(request);
		return "redirect: /admin/contributors";
	}

	@PutMapping("/{contributorId}")
	@ResponseBody
	public ResponseEntity<Void> updateContributor(@PathVariable Long contributorId, @RequestBody ContributorUpdateRequest request){
		feignClient.updateContributor(contributorId, request);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{contributorId}")
	public ResponseEntity<Void> deleteContributor(@PathVariable Long contributorId){
		feignClient.deleteContributor(contributorId);
		return ResponseEntity.noContent().build();
	}
}
