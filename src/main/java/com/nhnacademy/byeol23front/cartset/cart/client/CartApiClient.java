package com.nhnacademy.byeol23front.cartset.cart.client;

import com.nhnacademy.byeol23front.cartset.cart.dto.CartBookUpdateRequest;
import com.nhnacademy.byeol23front.cartset.cart.dto.CartResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "BYEOL23-GATEWAY", contextId = "cartApiClient")
public interface CartApiClient {

    @GetMapping("/api/carts/{memberId}")
    CartResponse getCartByMember(@PathVariable("memberId") Long memberId);

    @PutMapping("/api/cart-books")
    void updateCartBook(@RequestBody CartBookUpdateRequest request);

    @DeleteMapping("/api/cart-books/{cartBookId}")
    void deleteCartBook(@PathVariable("cartBookId") Long cartBookId);

}

