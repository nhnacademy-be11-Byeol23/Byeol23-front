package com.nhnacademy.byeol23front.cartset.cart.client;

import com.nhnacademy.byeol23front.cartset.cart.dto.CartBookAddRequest;
import com.nhnacademy.byeol23front.cartset.cart.dto.CartBookResponse;
import com.nhnacademy.byeol23front.cartset.cart.dto.CartBookUpdateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "BYEOL23-GATEWAY", contextId = "cartApiClient")
public interface CartApiClient {
    @GetMapping("/api/carts/books")
     List<CartBookResponse> getCartBooks();

    @PostMapping("/api/carts/books")
    void addCartBook(@RequestBody CartBookAddRequest request);

    @PostMapping("/api/carts/books/{book-id}/update")
    void updateCartBook(@PathVariable("book-id") Long bookId, @RequestBody CartBookUpdateRequest request);
}

