package com.nhnacademy.byeol23front.orderset.order.dto;

import java.time.LocalDateTime;

public record OrderInfoResponse(String orderNumber,
								LocalDateTime orderDate,
								String receiver,
								String actualOrderPrice,
								String orderStatus) {

}
