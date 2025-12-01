package com.nhnacademy.byeol23front.memberset.member.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.nhnacademy.byeol23front.couponset.coupon.client.CouponApiClient;
import com.nhnacademy.byeol23front.couponset.coupon.dto.IssuedCouponInfoResponseDto;
import com.nhnacademy.byeol23front.couponset.coupon.dto.UsedCouponInfoResponseDto;
import com.nhnacademy.byeol23front.likeset.client.LikeApiClient;
import com.nhnacademy.byeol23front.likeset.dto.LikeResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.nhnacademy.byeol23front.memberset.addresses.client.AddressApiClient;
import com.nhnacademy.byeol23front.memberset.addresses.dto.AddressResponse;
import com.nhnacademy.byeol23front.memberset.member.client.MemberApiClient;
import com.nhnacademy.byeol23front.memberset.member.dto.MemberMyPageResponse;
import com.nhnacademy.byeol23front.minio.dto.back.GetUrlResponse;
import com.nhnacademy.byeol23front.minio.service.MinioService;
import com.nhnacademy.byeol23front.minio.util.ImageDomain;
import com.nhnacademy.byeol23front.orderset.order.client.OrderApiClient;
import com.nhnacademy.byeol23front.orderset.order.controller.OrderUtil;
import com.nhnacademy.byeol23front.orderset.order.dto.OrderDetailResponse;
import com.nhnacademy.byeol23front.point.client.PointHistoryFeignClient;

@Slf4j
@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MypageController {
	private final MemberApiClient memberApiClient;
	private final OrderApiClient orderApiClient;
	private final MinioService minioService;
	private final AddressApiClient addressApiClient;
	private final PointHistoryFeignClient pointHistoryFeignClient;
	private final CouponApiClient couponApiClient;
	private final OrderUtil orderUtil;
	private final LikeApiClient likeApiClient;

	@ModelAttribute("activeTab")
	public String addActiveTabToModel(HttpServletRequest request) {
		String uri = request.getRequestURI();
		String activeTab = uri.substring(uri.lastIndexOf('/') + 1);

		if (activeTab.equals("mypage")) {
			return null;
		}
		return activeTab;
	}

	@ModelAttribute("member")
	public MemberMyPageResponse addMemberInfoToModel() {
		return memberApiClient.getMember();
	}

	@GetMapping
	public String getMypage(Model model) {
		log.info("mypage entrance");
		MemberMyPageResponse resp = memberApiClient.getMember();
		model.addAttribute("activeTab", "settings");
		model.addAttribute("member", resp);
		return "mypage/settings";
	}

	@GetMapping("/orders")
	public String getOrder(@PageableDefault(size = 10) Pageable pageable, Model model) {
		ResponseEntity<Page<OrderDetailResponse>> response = orderApiClient.getOrders(pageable);
		Page<OrderDetailResponse> orders = response.getBody();

		model.addAttribute("orders", orders);

		return "mypage/orders";
	}

	@GetMapping("/orders/{order-number}")
	public String getOrderDetails(Model model, @PathVariable(name = "order-number") String orderNumber) {
		ResponseEntity<OrderDetailResponse> response = orderApiClient.getOrderByOrderNumber(orderNumber);

		model.addAttribute("orderDetail", response.getBody());
		orderUtil.addFinalPaymentAmountToModel(model, response.getBody());

		return "mypage/order-detail";
	}

	@GetMapping("/wishlist")
	public String getWishlist(Model model) {
		model.addAttribute("activeTab", "wishlist");
		try {
			List<LikeResponse> likes = likeApiClient.getLikes();
			model.addAttribute("likes", likes);
		} catch (Exception e) {
			log.warn("위시리스트 조회 실패: {}", e.getMessage());
			model.addAttribute("likes", new ArrayList<LikeResponse>());
		}
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

	@GetMapping("/points")
	public String getPoints(Model model) {
		model.addAttribute("activeTab", "points");
		model.addAttribute("pointsHistories",
			pointHistoryFeignClient.getPointHistories());
		return "mypage/points_history";
	}

	@GetMapping("/addresses")
	public String getAddresses(Model model) {
		List<AddressResponse> addressList = addressApiClient.getAddresses().getBody();

		model.addAttribute("addresses", addressList);

		return "mypage/address";
	}

	@GetMapping("/settings")
	public String getSettings(Model model) {
		MemberMyPageResponse resp = memberApiClient.getMember();
		model.addAttribute("member", resp);
		return "mypage/settings";
	}

	@GetMapping("/coupons")
	public String getCoupons(Model model) {
		model.addAttribute("activeTab", "coupons");

		// 발급 내역(사용 전)
		List<IssuedCouponInfoResponseDto> issuedCoupons = couponApiClient.getIssuedCoupons().getBody();
		model.addAttribute("issuedCoupons", issuedCoupons);

		// 사용 내역
		List<UsedCouponInfoResponseDto> usedCoupons = couponApiClient.getUsedCoupons().getBody();
		model.addAttribute("usedCoupons", usedCoupons);
		return "mypage/coupon_box";
	}

}
