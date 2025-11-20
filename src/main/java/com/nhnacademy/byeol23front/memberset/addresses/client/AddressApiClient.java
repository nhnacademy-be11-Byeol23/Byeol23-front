package com.nhnacademy.byeol23front.memberset.addresses.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.nhnacademy.byeol23front.memberset.addresses.dto.AddressRequest;
import com.nhnacademy.byeol23front.memberset.addresses.dto.AddressResponse;

@FeignClient(name = "BYEOL23-GATEWAY", contextId = "addressApiClient")
public interface AddressApiClient {

	@GetMapping("/api/addresses")
	ResponseEntity<List<AddressResponse>> getAddresses();

	@PostMapping("/api/addresses")
	ResponseEntity<AddressResponse> createAddress(@RequestBody AddressRequest request);

	@PutMapping("/api/addresses")
	ResponseEntity<Void> updateAddress(@RequestBody AddressRequest request);

	@DeleteMapping("/api/addresses/{address-id}")
	ResponseEntity<Void> deleteAddress(@PathVariable(name = "address-id") Long addressId);

	@PostMapping("/api/addresses/{address-id}")
	ResponseEntity<Void> setDefaultAddress(@PathVariable(name = "address-id") Long addressId);
}
