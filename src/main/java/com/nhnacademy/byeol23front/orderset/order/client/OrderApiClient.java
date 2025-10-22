package com.nhnacademy.byeol23front.orderset.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.nhnacademy.byeol23front.orderset.order.dto.OrderPrepareRequest;
import com.nhnacademy.byeol23front.orderset.order.dto.OrderPrepareResponse;

@FeignClient(name = "orderApiClient", url = "http://localhost:10332")
public interface OrderApiClient {

	@PostMapping(value = "/api/order")
	ResponseEntity<OrderPrepareResponse> prepareOrder(@RequestBody OrderPrepareRequest request);

}
