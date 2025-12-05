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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.nhnacademy.byeol23front.commons.exception.ErrorResponse;

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
@Tag(name = "Mypage", description = "마이페이지 관련 API")
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
	@Operation(summary = "마이페이지 설정 화면", description = "마이페이지의 설정 탭을 표시합니다. 회원 정보를 조회하여 모델에 추가합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "마이페이지 설정 화면 반환 성공"),
		@ApiResponse(responseCode = "401", description = "인증 실패",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public String getMypage(Model model) {
		MemberMyPageResponse resp = memberApiClient.getMember();
		model.addAttribute("activeTab", "settings");
		model.addAttribute("member", resp);
		return "mypage/settings";
	}

	@GetMapping("/orders")
	@Operation(summary = "주문 내역 조회", description = "현재 로그인한 사용자의 주문 내역을 페이지네이션으로 조회합니다. 기본 페이지 크기는 10입니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "주문 내역 조회 성공"),
		@ApiResponse(responseCode = "401", description = "인증 실패",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public String getOrder(@PageableDefault(size = 10) Pageable pageable, Model model) {
		ResponseEntity<Page<OrderDetailResponse>> response = orderApiClient.getOrders(pageable);
		Page<OrderDetailResponse> orders = response.getBody();

		model.addAttribute("orders", orders);

		return "mypage/orders";
	}

	@GetMapping("/orders/{order-number}")
	@Operation(summary = "주문 상세 내역 조회", description = "주문 번호를 기반으로 주문의 상세 내역을 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "주문 상세 내역 조회 성공"),
		@ApiResponse(responseCode = "401", description = "인증 실패",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public String getOrderDetails(Model model, @PathVariable(name = "order-number") String orderNumber) {
		ResponseEntity<OrderDetailResponse> response = orderApiClient.getOrderByOrderNumber(orderNumber);

		model.addAttribute("orderDetail", response.getBody());
		orderUtil.addFinalPaymentAmountToModel(model, response.getBody());

		return "mypage/order-detail";
	}

	@GetMapping("/wishlist")
	@Operation(summary = "위시리스트 조회", description = "현재 로그인한 사용자가 좋아요를 누른 도서 목록을 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "위시리스트 조회 성공"),
		@ApiResponse(responseCode = "401", description = "인증 실패",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
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
	@Operation(summary = "쿠폰 지갑 화면", description = "쿠폰 지갑 화면을 표시합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "쿠폰 지갑 화면 반환 성공"),
		@ApiResponse(responseCode = "401", description = "인증 실패",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public String getWallet(Model model) {
		model.addAttribute("activeTab", "wallet");

		return "mypage/coupons";
	}

	@GetMapping("/reviews")
	@Operation(summary = "리뷰 내역 조회", description = "현재 로그인한 사용자가 작성한 리뷰 내역을 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "리뷰 내역 조회 성공"),
		@ApiResponse(responseCode = "401", description = "인증 실패",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public String getReviews(Model model) {

		return "mypage/reviews";
	}

	@GetMapping("/points")
	@Operation(summary = "포인트 내역 조회", description = "현재 로그인한 사용자의 포인트 적립/사용 내역을 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "포인트 내역 조회 성공"),
		@ApiResponse(responseCode = "401", description = "인증 실패",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public String getPoints(Model model) {
		model.addAttribute("activeTab", "points");
		model.addAttribute("pointsHistories",
			pointHistoryFeignClient.getPointHistories());
		return "mypage/points_history";
	}

	@GetMapping("/addresses")
	@Operation(summary = "배송지 목록 조회", description = "현재 로그인한 사용자가 등록한 배송지 목록을 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "배송지 목록 조회 성공"),
		@ApiResponse(responseCode = "401", description = "인증 실패",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public String getAddresses(Model model) {
		List<AddressResponse> addressList = addressApiClient.getAddresses().getBody();

		model.addAttribute("addresses", addressList);

		return "mypage/address";
	}

	@GetMapping("/settings")
	@Operation(summary = "설정 화면", description = "마이페이지의 설정 화면을 표시합니다. 회원 정보를 조회하여 모델에 추가합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "설정 화면 반환 성공"),
		@ApiResponse(responseCode = "401", description = "인증 실패",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public String getSettings(Model model) {
		MemberMyPageResponse resp = memberApiClient.getMember();
		model.addAttribute("member", resp);
		return "mypage/settings";
	}

	@GetMapping("/coupons")
	@Operation(summary = "쿠폰 내역 조회", description = "현재 로그인한 사용자의 발급된 쿠폰(사용 전)과 사용한 쿠폰 내역을 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "쿠폰 내역 조회 성공"),
		@ApiResponse(responseCode = "401", description = "인증 실패",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
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
