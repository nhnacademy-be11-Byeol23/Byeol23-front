package com.nhnacademy.byeol23front.orderset.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.nhnacademy.byeol23front.bookset.book.dto.BookOrderInfoResponse;

public record OrderDetailResponse(String orderNumber,
								  LocalDateTime orderDate,
								  String orderStatus,
								  BigDecimal actualOrderPrice,
								  String receiver,
								  String receiverPhone,
								  String receiverAddress,
								  String receiverAddressDetail,
								  String postCode,
								  List<BookOrderInfoResponse> items
								  ) {
}
