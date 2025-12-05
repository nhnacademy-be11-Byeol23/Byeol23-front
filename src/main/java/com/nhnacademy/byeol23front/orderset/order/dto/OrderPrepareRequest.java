package com.nhnacademy.byeol23front.orderset.order.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.nhnacademy.byeol23front.bookset.book.dto.BookInfoRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrderPrepareRequest(
	@NotNull BigDecimal totalBookPrice,
	@NotNull BigDecimal actualOrderPrice,

	@NotBlank(message = "수령인 이름은 필수입니다.")
	String receiver,

	@NotBlank(message = "우편번호는 필수입니다.")
	String postCode,

	@NotBlank(message = "주소는 필수입니다.")
	String receiverAddress,

	@NotBlank(message = "상세 주소는 필수입니다.")
	String receiverAddressDetail,

	String receiverAddressExtra,

	@NotBlank(message = "연락처는 필수입니다.")
	String receiverPhone,

	@NotNull(message = "도착 희망일은 필수입니다.")
	LocalDate deliveryArrivedDate,

	@NotNull(message = "주문 상품 목록은 비어 있을 수 없습니다.")
	List<BookInfoRequest> bookInfoRequestList,

	String orderPassword,

	BigDecimal usedPoints,

	Boolean isCartCheckout
) {}
