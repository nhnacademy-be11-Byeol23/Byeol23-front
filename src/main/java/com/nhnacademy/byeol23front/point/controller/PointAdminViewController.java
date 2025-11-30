// java
package com.nhnacademy.byeol23front.point.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

import com.nhnacademy.byeol23front.point.dto.ReservedPolicy; // 추가

@Controller
@RequestMapping("/admin/point-policy")
@RequiredArgsConstructor
public class PointAdminViewController {
	@GetMapping
	public String pointPolicyPage(Model model) {
		model.addAttribute("pageTitle", "포인트 정책 관리");
		// ReservedPolicy 값을 뷰에서 선택할 수 있도록 추가
		model.addAttribute("reservedPolicies", ReservedPolicy.values());
		return "admin/point/point_policy";
	}
}
