package com.nhnacademy.byeol23front.point.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.byeol23front.auth.Authorized;
import com.nhnacademy.byeol23front.auth.Role;
import com.nhnacademy.byeol23front.point.client.PointHistoryFeignClient;
import com.nhnacademy.byeol23front.point.dto.PointHistoryDTO;

import lombok.RequiredArgsConstructor;

@Authorized(role = Role.USER)
@RestController
@RequestMapping("/api/front/own-points")
@RequiredArgsConstructor
public class OwnPointController {
	private final PointHistoryFeignClient pointHistoryFeignClient;

	@GetMapping
	public List<PointHistoryDTO> getOwnPointHistories() {
		return pointHistoryFeignClient.getPointHistories();
	}
}
