package com.nhnacademy.byeol23front.memberset.member.controller;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.nhnacademy.byeol23front.memberset.member.client.MemberApiClient;
import com.nhnacademy.byeol23front.memberset.member.dto.MemberMyPageResponse;
import com.nhnacademy.byeol23front.orderset.order.client.OrderApiClient;
import com.nhnacademy.byeol23front.orderset.order.dto.OrderDetailResponse;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MypageController {
	private final MemberApiClient memberApiClient;
	private final OrderApiClient orderApiClient;

	@ModelAttribute("activeTab")
	public String addActiveTabToModel(HttpServletRequest request) {
		String uri = request.getRequestURI();
		String activeTab = uri.substring(uri.lastIndexOf('/') + 1);

		if (activeTab.equals("mypage")) {
			return null;
		}
		return activeTab;
	}

	@GetMapping
	public String getMypage(Model model) {
		ResponseEntity<MemberMyPageResponse> response = memberApiClient.getMember();

		model.addAttribute("member", response.getBody());

		return "mypage/mypage";
	}

	@GetMapping("/orders")
	public String getOrder(Model model) {

		ResponseEntity<List<OrderDetailResponse>> orders = orderApiClient.getOrders();

		model.addAttribute("orders", orders.getBody());

		return "mypage/orders";
	}

	@GetMapping("/wishlist")
	public String getWishlist(Model model) {
		model.addAttribute("activeTab", "wishlist");
		return "mypage/wishlist";
	}

	@GetMapping("/wallet")
	public String getWallet(Model model) {
		model.addAttribute("activeTab", "wallet");

		return "mypage/coupons";
	}

	@GetMapping("/reviews")
	public String getReviews(Model model) {

		return "mypage/reviews";
	}

	@GetMapping("/addresses")
	public String getAddresses(Model model) {
		ResponseEntity<MemberMyPageResponse> response = memberApiClient.getMember();

		return "mypage/address";
	}

	@GetMapping("/settings")
	public String getSettings(Model model) {

		return "mypage/settings";
	}

}
