package com.nhnacademy.byeol23front.point.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.nhnacademy.byeol23front.point.dto.PointHistoryDTO;

@FeignClient(name = "BYEOL23-BACKEND", contextId = "pointHistoryFeignClient")
public interface PointHistoryFeignClient {
	@GetMapping("/api/point-histories")
	public List<PointHistoryDTO> getPointHistories();
}
