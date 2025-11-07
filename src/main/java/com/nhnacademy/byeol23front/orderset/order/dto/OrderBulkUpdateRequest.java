package com.nhnacademy.byeol23front.orderset.order.dto;

import java.util.List;

public record OrderBulkUpdateRequest(List<String> orderNumberLists,
									 String status) {
}
