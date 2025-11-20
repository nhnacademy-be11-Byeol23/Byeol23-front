package com.nhnacademy.byeol23front.memberset.member.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
import org.springframework.web.bind.annotation.ResponseBody;

import com.nhnacademy.byeol23front.memberset.addresses.client.AddressApiClient;
import com.nhnacademy.byeol23front.memberset.addresses.dto.AddressResponse;
import com.nhnacademy.byeol23front.memberset.member.client.MemberApiClient;
import com.nhnacademy.byeol23front.memberset.member.dto.MemberMyPageResponse;
import com.nhnacademy.byeol23front.minio.dto.back.GetUrlResponse;
import com.nhnacademy.byeol23front.minio.service.MinioService;
import com.nhnacademy.byeol23front.minio.util.ImageDomain;
import com.nhnacademy.byeol23front.orderset.order.client.OrderApiClient;
import com.nhnacademy.byeol23front.orderset.order.dto.OrderDetailResponse;

@Slf4j
@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MypageController {
	private final MemberApiClient memberApiClient;
	private final OrderApiClient orderApiClient;
	private final MinioService minioService;
	private final AddressApiClient addressApiClient;

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
		ResponseEntity<MemberMyPageResponse> response = memberApiClient.getMember();
		return response.getBody();
	}

	@GetMapping
	public String getMypage(Model model) {
		ResponseEntity<MemberMyPageResponse> response = memberApiClient.getMember();

		model.addAttribute("member", response.getBody());

		return "mypage/mypage";
	}

	@GetMapping("/orders")
	public String getOrder(@PageableDefault(size = 10) Pageable pageable, Model model) {
		ResponseEntity<Page<OrderDetailResponse>> response = orderApiClient.getOrders(pageable);
		Page<OrderDetailResponse> orders = response.getBody();

		List<OrderViewModel> orderViewModels = new ArrayList<>();
		String defaultImageUrl = "https://image.yes24.com/momo/Noimg_L.jpg";

		if (!Objects.isNull(orders)) {
			for (OrderDetailResponse order : orders) {
				String imageUrl = defaultImageUrl;

				if (order.items() != null && !order.items().isEmpty()) {
					Long firstBookId = order.items().get(0).bookId();

					try {
						List<GetUrlResponse> images = minioService.getImageUrl(ImageDomain.BOOK, firstBookId);

						if (images != null && !images.isEmpty()) {
							imageUrl = images.get(0).imageUrl();
						}
					} catch (Exception e) {
						log.warn("Failed to get image for bookId {}: {}", firstBookId, e.getMessage());
					}
				}
				orderViewModels.add(new OrderViewModel(order, imageUrl));
			}
		}

		model.addAttribute("viewModels", orderViewModels);
		model.addAttribute("orders", orders);

		return "mypage/orders";
	}

	@GetMapping("/orders/{order-number}")
	@ResponseBody
	public OrderDetailResponse getOrderDetails(@PathVariable(name = "order-number")String orderNumber) {
		ResponseEntity<OrderDetailResponse> response = orderApiClient.getOrderByOrderNumber(orderNumber);

		return response.getBody();
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
		List<AddressResponse> addressList = addressApiClient.getAddresses().getBody();

		model.addAttribute("addresses", addressList);

		return "mypage/address";
	}

	@GetMapping("/settings")
	public String getSettings(Model model) {

		return "mypage/settings";
	}

	public record OrderViewModel(OrderDetailResponse order, String firstImageUrl) { }

}
