package com.nhnacademy.byeol23front.orderset.order.controller;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import com.nhnacademy.byeol23front.bookset.book.dto.BookInfoRequest;
import com.nhnacademy.byeol23front.bookset.book.dto.BookOrderRequest;
import com.nhnacademy.byeol23front.orderset.delivery.client.DeliveryApiClient;
import com.nhnacademy.byeol23front.orderset.delivery.dto.DeliveryPolicyInfoResponse;
import com.nhnacademy.byeol23front.orderset.packaging.client.PackagingApiClient;
import com.nhnacademy.byeol23front.orderset.packaging.dto.PackagingInfoResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderUtil {
	private final DeliveryApiClient deliveryApiClient;
	private final PackagingApiClient packagingApiClient;

	public void addDeliveryDatesToModel(Model model) {
		List<Map<String, String>> deliveryDate = new ArrayList<>();
		LocalDate today = LocalDate.now();
		LocalDate currentDate = today;
		int businessDaysCount = 0;
		LocalDate defaultDate = null;

		DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("M/d"); // 10/30
		DateTimeFormatter valueFormatter = DateTimeFormatter.ISO_LOCAL_DATE;

		while (businessDaysCount < 5) {
			currentDate = currentDate.plusDays(1);
			DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
			if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
				businessDaysCount++;
				String dayName = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN); // e.g 목
				String displayDate = currentDate.format(displayFormatter); // e.g 10/30
				String valueDate = currentDate.format(valueFormatter); // e.g 2025-10-30

				deliveryDate.add(Map.of(
					"dayName", dayName,
					"displayDate", displayDate,
					"valueDate", valueDate
				));

				if (businessDaysCount == 2) {
					defaultDate = currentDate;
				}
			}
		}

		model.addAttribute("deliveryDates", deliveryDate);
		model.addAttribute("defaultDeliveryDate", defaultDate != null ? defaultDate.format(valueFormatter) : "");
	}

	public void addDeliveryFeeToModel(Model model, BookOrderRequest request) {

		BigDecimal totalBookPrice = BigDecimal.ZERO;

		for (BookInfoRequest infoRequest : request.bookList()) {
			BigDecimal quantity = BigDecimal.valueOf(infoRequest.quantity());
			BigDecimal itemSubtotal = infoRequest.salePrice().multiply(quantity);
			totalBookPrice = totalBookPrice.add(itemSubtotal);
		}

		ResponseEntity<DeliveryPolicyInfoResponse> response = deliveryApiClient.getCurrentDeliveryPolicy();
		DeliveryPolicyInfoResponse deliveryPolicy = response.getBody();

		BigDecimal deliveryFee = BigDecimal.ZERO;
		BigDecimal actualOrderPrice = totalBookPrice;

		if (deliveryPolicy != null) {
			BigDecimal policyFee = deliveryPolicy.deliveryFee();
			BigDecimal freeThreshold = deliveryPolicy.freeDeliveryCondition();

			if (freeThreshold != null && freeThreshold.compareTo(BigDecimal.ZERO) > 0
				&& totalBookPrice.compareTo(freeThreshold) >= 0) {
				deliveryFee = BigDecimal.ZERO;
			} else {
				deliveryFee = policyFee != null ? policyFee : BigDecimal.ZERO;
			}
		} else {
			log.warn("배송비 정책을 가져올 수 없습니다. 기본 배송비 0원으로 처리합니다.");
		}

		actualOrderPrice = totalBookPrice.add(deliveryFee);

		model.addAttribute("totalBookPrice", totalBookPrice);
		model.addAttribute("deliveryFee", deliveryFee);
		model.addAttribute("actualOrderPrice", actualOrderPrice);
	}

	public void addOrderSummary(Model model, List<BookInfoRequest> requestList) {
		model.addAttribute("orderItem", requestList);
	}

	public void addTotalQuantity(Model model, List<BookInfoRequest> requestList) {
		int totalQuantity = requestList.stream()
			.mapToInt(BookInfoRequest::quantity)
			.sum();

		model.addAttribute("totalQuantity", totalQuantity);
	}

	public void addPackagingOption(Model model) {
		List<PackagingInfoResponse> packagingOptions = packagingApiClient.getAllPackagingLists();
		model.addAttribute("packagingOptions", packagingOptions);
	}
}
