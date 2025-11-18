// java
package com.nhnacademy.byeol23front.point.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nhnacademy.byeol23front.auth.Authorized;
import com.nhnacademy.byeol23front.auth.Role;
import com.nhnacademy.byeol23front.point.client.PointPolicyFeignClinet;
import com.nhnacademy.byeol23front.point.dto.PointPolicyDTO;

import io.swagger.v3.oas.annotations.Operation;

@Authorized(role = Role.ADMIN) // 인가처리를 백엔드에서 하기 때문에 삭제해야 됨
@RestController
@RequestMapping("/api/front/point-policies")
public class PointPolicyProxyController {

	private final PointPolicyFeignClinet pointPolicyFeignClinet;

	public PointPolicyProxyController(PointPolicyFeignClinet pointPolicyFeignClinet) {
		this.pointPolicyFeignClinet = pointPolicyFeignClinet;
	}

	@Operation(summary = "페이징된 모든 포인트 정책 조회 (프록시)")
	@GetMapping
	public ResponseEntity<List<PointPolicyDTO>> getAllPointPolicies(
		@RequestParam(value = "page", required = false) Integer page,
		@RequestParam(value = "size", required = false) Integer size,
		@RequestParam(value = "sort", required = false) List<String> sort
	) {
		return pointPolicyFeignClinet.getAllPointPolicies(page, size, sort);
	}

	@Operation(summary = "이름으로 포인트 정책 조회 (프록시)")
	@GetMapping("/{name}")
	public ResponseEntity<PointPolicyDTO> getPointPolicy(@PathVariable("name") String name) {
		return pointPolicyFeignClinet.getPointPolicy(name);
	}

	@Operation(summary = "포인트 정책 생성 (프록시)")
	@PostMapping("/create")
	public ResponseEntity<Void> createPointPolicy(@RequestBody PointPolicyDTO dto) {
		return pointPolicyFeignClinet.createPointPolicy(dto);
	}

	@Operation(summary = "포인트 정책 수정 (프록시)")
	@PutMapping("/update")
	public ResponseEntity<Void> updatePointPolicy(@RequestBody PointPolicyDTO dto) {
		return pointPolicyFeignClinet.updatePointPolicy(dto);
	}

	@Operation(summary = "포인트 정책 삭제 (프록시)")
	@DeleteMapping("/{name}")
	public ResponseEntity<Void> deletePointPolicy(@PathVariable("name") String name) {
		return pointPolicyFeignClinet.deletePointPolicy(name);
	}
}
