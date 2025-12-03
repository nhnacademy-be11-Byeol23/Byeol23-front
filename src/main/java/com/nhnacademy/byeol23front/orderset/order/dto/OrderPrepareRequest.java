package com.nhnacademy.byeol23front.orderset.order.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.nhnacademy.byeol23front.bookset.book.dto.BookInfoRequest;

public record OrderPrepareRequest(BigDecimal totalBookPrice,
								  BigDecimal actualOrderPrice,
								  String receiver,
								  String postCode,
								  String receiverAddress,
								  String receiverAddressDetail,
								  String receiverAddressExtra,
								  String receiverPhone,
								  LocalDate deliveryArrivedDate,
								  List<BookInfoRequest> bookInfoRequestList,
								  String orderPassword) {
}
