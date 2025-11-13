package com.nhnacademy.byeol23front.point.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nhnacademy.byeol23front.point.dto.PointPolicyDTO;

@FeignClient(name = "BYEOL23-BACKEND", contextId = "pointFeignClient")
public interface PointPolicyFeignClinet {

	@GetMapping("/api/point-policies")
	ResponseEntity<List<PointPolicyDTO>> getAllPointPolicies(
		@RequestParam(value = "page", required = false) Integer page,
		@RequestParam(value = "size", required = false) Integer size,
		@RequestParam(value = "sort", required = false) List<String> sort
	);

	@GetMapping("/api/point-policies/{name}")
	ResponseEntity<PointPolicyDTO> getPointPolicy(@PathVariable("name") String name);

	@PostMapping("/api/point-policies/create")
	ResponseEntity<Void> createPointPolicy(@RequestBody PointPolicyDTO pointPolicyDTO);

	@PutMapping("/api/point-policies/update")
	ResponseEntity<Void> updatePointPolicy(@RequestBody PointPolicyDTO pointPolicyDTO);

	@DeleteMapping("/api/point-policies/{name}")
	ResponseEntity<Void> deletePointPolicy(@PathVariable("name") String name);
}
