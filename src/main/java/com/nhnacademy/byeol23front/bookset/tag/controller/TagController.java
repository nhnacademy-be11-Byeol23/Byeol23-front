package com.nhnacademy.byeol23front.bookset.tag.controller;

import org.springframework.http.HttpStatus;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nhnacademy.byeol23front.bookset.tag.client.TagApiClient;
import com.nhnacademy.byeol23front.bookset.tag.dto.AllTagsInfoResponse;
import com.nhnacademy.byeol23front.bookset.tag.dto.PageResponse;
import com.nhnacademy.byeol23front.bookset.tag.dto.TagCreateRequest;
import com.nhnacademy.byeol23front.bookset.tag.dto.TagCreateResponse;
import com.nhnacademy.byeol23front.bookset.tag.dto.TagUpdateRequest;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/tags")
public class TagController {
	private final TagApiClient feignClient;

	@GetMapping
	public String getTags(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		Model model
		){
		PageResponse<AllTagsInfoResponse> response = feignClient.getAllTags(page, size).getBody();
		model.addAttribute("tags", response.content());
		model.addAttribute("paging", response);
		return "admin/tags/tag";
	}

	@PostMapping
	@ResponseBody
	public TagCreateResponse createTag(@RequestBody TagCreateRequest request){
		ResponseEntity<TagCreateResponse> response = feignClient.createTag(request);
		return response.getBody();
	}

	@ResponseBody
	@PostMapping("/delete/{tag-id}")
	public ResponseEntity<Void> deleteTag(@PathVariable(name = "tag-id") Long tagId){
		feignClient.deleteTag(tagId);
		return ResponseEntity.ok().build();
	}

	@ResponseBody
	@PostMapping("/put/{tag-id}")
	public ResponseEntity<Void> updateTag(@PathVariable(name = "tag-id") Long tagId, @RequestBody TagUpdateRequest tagName){
		// try {
		// 	feignClient.updateTag(tagId, tagName);
		// } catch(RuntimeException e){
		// 	return ResponseEntity.status(HttpStatus.CONFLICT).build();
		// }
		feignClient.updateTag(tagId, tagName);
		return ResponseEntity.ok().build();
	}
}
