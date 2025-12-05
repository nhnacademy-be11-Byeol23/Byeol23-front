package com.nhnacademy.byeol23front.point.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nhnacademy.byeol23front.auth.Authorized;
import com.nhnacademy.byeol23front.auth.Role;
import com.nhnacademy.byeol23front.point.client.PointPolicyFeignClinet;
import com.nhnacademy.byeol23front.point.dto.PointPolicyDTO;
import com.nhnacademy.byeol23front.point.dto.ReservedPolicy;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;

@Authorized(role = Role.ADMIN)
@RestController
@Slf4j
@RequestMapping("/api/front/point-policies")
public class PointPolicyProxyController {

	private final PointPolicyFeignClinet pointPolicyFeignClinet;

	public PointPolicyProxyController(PointPolicyFeignClinet pointPolicyFeignClinet) {
		this.pointPolicyFeignClinet = pointPolicyFeignClinet;
	}

	@Operation(summary = "모든 포인트 정책 조회 (프록시)")
	@GetMapping
	public ResponseEntity<Map<ReservedPolicy, List<PointPolicyDTO>>> getAllPointPolicies(){

		Map<ReservedPolicy, List<PointPolicyDTO>> body = pointPolicyFeignClinet.getAllPointPolicies();
		for(List<PointPolicyDTO> list : body.values()){
			for(PointPolicyDTO policy : list){
				log.info("Policy Retrieved: {}", policy.pointPolicyName());
			}
		}
		return ResponseEntity.ok(body);
	}

	@Operation(summary = "포인트 정책 생성 (프록시)")
	@PostMapping("/create")
	public ResponseEntity<Void> createPointPolicy(@RequestBody PointPolicyDTO dto) {
		pointPolicyFeignClinet.createPointPolicy(dto);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "포인트 정책 수정 (프록시)")
	@PostMapping("/update/{id}")
	public ResponseEntity<Void> updatePointPolicy(@PathVariable("id") Long id, @RequestBody PointPolicyDTO dto) {
		pointPolicyFeignClinet.updatePointPolicy(id, dto);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/{id}")
	public ResponseEntity<PointPolicyDTO> getPointPolicyById(@PathVariable("id") Long id) {
		PointPolicyDTO dto = pointPolicyFeignClinet.getPointPolicyById(id);
		return ResponseEntity.ok(dto);
	}

	@Operation(summary = "포인트 정책 삭제 (프록시)")
	@PostMapping("/{id}")
	public ResponseEntity<Void> deletePointPolicy(@PathVariable("id") Long id) {
		pointPolicyFeignClinet.deletePointPolicy(id);
		return ResponseEntity.noContent().build();
	}

	@Operation(summary = "포인트 정책 활성화 (프록시)")
	@PostMapping("/activate/{id}")
	public ResponseEntity<Void> activatePointPolicy(@PathVariable("id") Long id) {
		pointPolicyFeignClinet.activatePointPolicy(id);
		return ResponseEntity.ok().build();
	}
}
