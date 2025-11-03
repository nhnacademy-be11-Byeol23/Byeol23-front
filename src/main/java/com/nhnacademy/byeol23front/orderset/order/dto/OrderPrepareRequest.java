package com.nhnacademy.byeol23front.orderset.order.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OrderPrepareRequest(BigDecimal totalBookPrice,
								  BigDecimal actualOrderPrice,
								  String receiver,
								  String postCode,
								  String receiverAddress,
								  String receiverAddressDetail,
								  String receiverAddressExtra,
								  String receiverPhone,
								  LocalDate deliveryArrivedDate) {
}
