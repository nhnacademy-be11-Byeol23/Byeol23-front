package com.nhnacademy.byeol23front.orderset.order.dto;

import java.math.BigDecimal;

public record OrderCreateRequest(BigDecimal totalBookPrice,
								 BigDecimal actualOrderPrice,
								 String receiver,
								 String receiverAddress,
								 String receiverAddressDetail,
								 String receiverPhone) {
}
