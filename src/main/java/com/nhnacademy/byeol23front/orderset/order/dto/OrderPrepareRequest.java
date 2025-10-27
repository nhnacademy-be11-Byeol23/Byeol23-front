package com.nhnacademy.byeol23front.orderset.order.dto;

import java.math.BigDecimal;

public record OrderPrepareRequest(BigDecimal totalBookPrice,
								  BigDecimal actualOrderPrice,
								  String receiver,
								  String postCode,
								  String receiverAddress,
								  String receiverAddressDetail,
								  String receiverPhone) {
}
