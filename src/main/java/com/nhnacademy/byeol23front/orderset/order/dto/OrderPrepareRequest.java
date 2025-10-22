package com.nhnacademy.byeol23front.orderset.order.dto;

import java.math.BigDecimal;

public record OrderPrepareRequest(String orderNumber,
								  BigDecimal totalBookPrice,
								  BigDecimal actualOrderPrice,
								  String receiver,
								  String postCode,
								  String receiverAddress,
								  String receiverAddressDetail,
								  String receiverPhone) {
}
