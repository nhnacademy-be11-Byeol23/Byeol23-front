// java
package com.nhnacademy.byeol23front.point.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/point-policy")
@RequiredArgsConstructor
public class PointAdminViewController {
	@GetMapping
	public String pointPolicyPage(Model model) {
		model.addAttribute("pageTitle", "포인트 정책 관리");
		return "admin/point/point_policy";
	}
}
