// java
package com.nhnacademy.byeol23front.point.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nhnacademy.byeol23front.point.client.PointFeignClinet;
import com.nhnacademy.byeol23front.point.dto.PointPolicyDTO;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/front/point-policies")
public class PointPolicyProxyController {

	private final PointFeignClinet pointFeignClinet;

	public PointPolicyProxyController(PointFeignClinet pointFeignClinet) {
		this.pointFeignClinet = pointFeignClinet;
	}

	@Operation(summary = "페이징된 모든 포인트 정책 조회 (프록시)")
	@GetMapping
	public ResponseEntity<List<PointPolicyDTO>> getAllPointPolicies(
		@RequestParam(value = "page", required = false) Integer page,
		@RequestParam(value = "size", required = false) Integer size,
		@RequestParam(value = "sort", required = false) List<String> sort
	) {
		return pointFeignClinet.getAllPointPolicies(page, size, sort);
	}

	@Operation(summary = "이름으로 포인트 정책 조회 (프록시)")
	@GetMapping("/{name}")
	public ResponseEntity<PointPolicyDTO> getPointPolicy(@PathVariable("name") String name) {
		return pointFeignClinet.getPointPolicy(name);
	}

	@Operation(summary = "포인트 정책 생성 (프록시)")
	@PostMapping("/create")
	public ResponseEntity<Void> createPointPolicy(@RequestBody PointPolicyDTO dto) {
		return pointFeignClinet.createPointPolicy(dto);
	}

	@Operation(summary = "포인트 정책 수정 (프록시)")
	@PutMapping("/update")
	public ResponseEntity<Void> updatePointPolicy(@RequestBody PointPolicyDTO dto) {
		return pointFeignClinet.updatePointPolicy(dto);
	}

	@Operation(summary = "포인트 정책 삭제 (프록시)")
	@DeleteMapping("/{name}")
	public ResponseEntity<Void> deletePointPolicy(@PathVariable("name") String name) {
		return pointFeignClinet.deletePointPolicy(name);
	}
}
