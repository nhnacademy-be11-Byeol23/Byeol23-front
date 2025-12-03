package com.nhnacademy.byeol23front.memberset.addresses.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.byeol23front.memberset.addresses.client.AddressApiClient;
import com.nhnacademy.byeol23front.memberset.addresses.dto.AddressRequest;
import com.nhnacademy.byeol23front.memberset.addresses.dto.AddressResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
public class AddressController {
	private final AddressApiClient addressApiClient;

	@PostMapping
	public AddressResponse createAddress(@RequestBody AddressRequest request) {
		return addressApiClient.createAddress(request).getBody();
	}

	@PutMapping
	public ResponseEntity<Void> updateAddress(@RequestBody AddressRequest request) {
		return addressApiClient.updateAddress(request);
	}

	@DeleteMapping("/{address-id}")
	public ResponseEntity<Void> deleteAddress(@PathVariable(name = "address-id") Long addressId) {
		return addressApiClient.deleteAddress(addressId);
	}

	@PostMapping("/{address-id}")
	public ResponseEntity<Void> setDefaultAddress(@PathVariable(name = "address-id") Long addressId) {
		return addressApiClient.setDefaultAddress(addressId);
	}

	@GetMapping("/me")
	public ResponseEntity<List<AddressResponse>>getAddresses() {
		return ResponseEntity.ok(addressApiClient.getAddresses().getBody());
	}

}