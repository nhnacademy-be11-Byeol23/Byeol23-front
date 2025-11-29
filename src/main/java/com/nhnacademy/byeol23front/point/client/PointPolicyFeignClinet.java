package com.nhnacademy.byeol23front.point.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nhnacademy.byeol23front.point.dto.PointPolicyDTO;
import com.nhnacademy.byeol23front.point.dto.ReservedPolicy;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "BYEOL23-BACKEND", contextId = "pointFeignClient", path = "/api/point-policies")
public interface PointPolicyFeignClinet {

	@GetMapping
	Map<ReservedPolicy, List<PointPolicyDTO>> getAllPointPolicies();

	@PostMapping
	void createPointPolicy(@RequestBody PointPolicyDTO pointPolicyDTO);

	@PostMapping("update/{id}")
	void updatePointPolicy(@PathVariable("id") Long id, @RequestBody PointPolicyDTO pointPolicyDTO);

	@PostMapping("delete/{id}")
	void deletePointPolicy(@PathVariable("id") Long id);

	@PostMapping("activate/{id}")
	void activatePointPolicy(@PathVariable("id") Long id);

	@GetMapping("/{id}")
	PointPolicyDTO getPointPolicyById(@PathVariable("id") Long id);
}