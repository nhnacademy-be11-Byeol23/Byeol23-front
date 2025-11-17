package com.nhnacademy.byeol23front.memberset.member.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.nhnacademy.byeol23front.memberset.member.client.MemberApiClient;
import com.nhnacademy.byeol23front.memberset.member.dto.MemberMyPageResponse;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MypageController {
    private final MemberApiClient memberApiClient;

    @GetMapping
    public String getMypage(Model model, @CookieValue(name = "Access-Token") String token) {

        ResponseEntity<MemberMyPageResponse> response = memberApiClient.getMember(token);

        model.addAttribute("member", response.getBody());

        return "account";
    }

    @GetMapping("/orders")
    public String getOrder(Model model, @CookieValue(name = "Access-Token") String token) {

        ResponseEntity<MemberMyPageResponse> response = memberApiClient.getMember(token);

        model.addAttribute("member", response.getBody());

        return "myOrders";
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
		model.addAttribute("activeTab", "reviews");
		return "mypage/reviews";
	}

	@GetMapping("/addresses")
	public String getAddresses(Model model) {
		model.addAttribute("activeTab", "addresses");
		return "mypage/address";
	}

	@GetMapping("/settings")
	public String getSettings(Model model) {
		model.addAttribute("activeTab", "settings");
		return "mypage/settings";
	}

}
